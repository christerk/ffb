# Recreate a Game from a Replay Position

Status: **planned / not implemented**.
Scope: **BB2025** for the first version (resume sequences are ruleset specific). `bb2016` / `bb2020` can be
added later by implementing their resume sequences in their own packages (no inheritance between rulesets).

---

## 1. Goal

A coach watches a replay, stops at a position and starts a new game from exactly that state. The new game is
a **test game** (no FUMBBL game state, no result upload, deleted when closed). Getting into the game must be
easy:

* the initiating coach picks the team they coach (or only spectates),
* a second coach takes the opposing team,
* in a **collaborative replay** the initiator can assign another participant as opponent; all remaining
  participants become spectators,
* in a **solo replay** (session already closed, OTP already consumed) nobody types a password; coaches get a
  link they can click / copy-paste that starts the client in the right mode, reusing the FUMBBL JNLP flow.

### 1.1 Non-goals (v1)

* Recreating at *arbitrary* commands (mid-activation, open dialogs, mid kick-off), see §3.
* Uploading or persisting anything to FUMBBL, changing team data (SPP, injuries, treasury) of real teams.
* Continuing the recreated game as a competitive game.

---

## 2. Relevant existing mechanics

| Area | Where | Relevance |
|---|---|---|
| Replay reconstruction | `ClientReplayer.createGame()` / `addTeam()` / `replayTo()` (`ffb-client-logic/.../ClientReplayer.java:383-480`) | Builds a baseline `Game` from the final game and applies all `ServerCommand`s (model changes via `ModelChangeProcessor`, which lives in `ffb-common`). |
| Server log | `GameState.getGameLog()` / `GameState.toJsonValue()` (`ffb-server/.../GameState.java:352-480`) | Server has every replayable `ServerCommand` of a game, but its **step stack / server-only state only for the latest point** (end of game for finished games). |
| Loading old games | `ServerCommandHandlerReplay` (cache → `GameCache.queryFromDb` → `ServerRequestLoadReplay` from backup) | Same lookup chain can provide the source game. |
| Test games | `GameCache.createGameState(START_TEST_GAME)`, `game.isTesting()` | Skips `FumbblRequestCreate/Update/ResumeGamestate`, `FumbblRequestUploadResults`/`ServerRequestSaveReplay` (`StepEndGame:59`), ownership check (`UtilServerStartGame:128`), duplicate session closing, DB rows are deleted on removal (`GameCache:162`), test chat commands allowed (`TalkRequirements:67`). |
| Join by name | `ServerCommandHandlerJoinApproved` (`gameName` + `gameCache.mapGameNameToId`, `test:` prefix) | First joiner creates, later joiners find the game by the same name. Pattern to reuse for links. |
| Resume of started games | `joinWithoutTeam` → `UtilServerStartGame.startGame` | If `game.getStarted() != null` and the step stack is not empty, the game is just resumed (no FUMBBL team loading / gamestate check). |
| Collaborative replay | `ReplaySessionManager`, `ServerCommandHandlerJoinReplay`, `ServerCommandReplayControl` | Knows participants and the controlling coach. **Coach names come from `ClientCommandJoinReplay` and are not verified**; only clients started with `-auth` authenticated (`ReplayLogicModule.setUp`, `JoinApproved` REPLAY branch). |
| Solo replay | `ReplayLogicModule.replayMode(false, …)` → `sendCloseSession()` | No server connection afterwards; the OTP from the JNLP was already consumed. |
| Client start | `ClientParameters` (`-player/-spectator/-replay`, `-coach`, `-gameId`, `-teamId`, `-auth`, …), `FantasyFootballClientAwt.main` | Extended with new arguments; the FUMBBL JNLP generator fills `-coach` and `-auth <OTP>`. |

---

## 3. Core design decision: server rebuilds the state, only at resume anchors

### 3.1 The snapshot is a reference, not data

A recreated game is identified by `(sourceGameId, commandNr)`. The **server** rebuilds the `Game` itself from
the source game's log; the client never uploads a game model. This

* prevents forged states (important even for test games, e.g. impossible rosters),
* makes the solo flow trivial: a link only has to carry `sourceGameId` and `commandNr`,
* guarantees client and server agree on the state.

### 3.2 Why anchors are needed

The server step stack at an arbitrary replay position is unknown (only the final `GameState` is stored), and
some state is server-only and not reconstructible from model changes: `PassState`, `BlitzTurnState`,
`PrayerState`, `ActiveEffects`, zapped player ids, kicking swarmers, plus parameters published on the step stack.
Therefore a game can only be recreated at **resume anchors**: positions where the step stack would be in a
well known, freshly pushable state and the server-only state is empty or derivable.

### 3.3 Anchors (v1)

`RecreationAnchorService` (new, `ffb-common`, instance methods, used by client *and* server):

* **A1 – between activations in a regular turn**: `turnMode == REGULAR`, no acting player
  (`actingPlayer.getPlayerId() == null`), no dialog parameter, `!waitingForOpponent`, game started and not
  finished, no pending turnover / touchdown (team turn not ended), ball not in the air (no pass/bomb/TTM
  in flight). This also covers "start of a team turn".
  Resume: push the ruleset's `Select` sequence (exactly what `StepEndFeeding` does after an activation).
* **A2 – (phase 2) start of setup** (`turnMode == SETUP` at the start of the kicking team's setup).
  Resume: push the part of the `Kickoff` sequence starting at setup.

Server-only state at A1:

| State | Handling |
|---|---|
| `PassState`, `BlitzTurnState`, kicking swarmers | Empty by definition of A1 (no ball in flight, not a kick-off blitz turn). |
| `ActiveEffects` (carried players etc.) | Must be empty at A1; anchor predicate rejects positions where the model shows such effects. |
| `PrayerState`, zapped players | Derive from the `Game` model where possible (prayer/zap effects are visible via player states/enhancements and inducements). If not derivable for a given ruleset feature, the anchor predicate rejects the position and the UI explains why. |

Future improvement (not needed for v1): persist a compact server-only state snapshot at every team turn start
in the game log (non-replayable internal entry). This would make anchors exact and allow more anchor types.

### 3.4 Server reconstruction

New `ReplayStateReconstructionService` (`ffb-common`, instance methods):

1. Extract the baseline creation from `ClientReplayer.createGame()` / `addTeam()` into this service
   (`createBaseline(Game finalGame)`), and let `ClientReplayer` delegate to it, so client and server use the
   identical algorithm.
2. `reconstruct(Game finalGame, ServerCommand[] log, int commandNr)` applies all commands up to `commandNr`
   with `ModelChangeProcessor` (model-sync commands only; reports/sounds are ignored).
3. Server validates the result with `RecreationAnchorService` (never trust the client's anchor claim).

Source loading: `RecreationSourceLoader` (server) reuses the lookup chain of `ServerCommandHandlerReplay`
(cache → DB → backup URL) but loads into a **detached** `GameState` that is *not* added to the `GameCache`
(avoids `LOADING` states and side effects on the source game). Async because the backup load is async; the
join/creation request waits on a callback via the request processor.

### 3.5 Creating the recreated game

`RecreatedGameFactory` (server, instance):

1. `gameCache.createGameState(GameStartMode.START_TEST_GAME)` → new id, `testing = true`.
2. Copy the reconstructed `Game` into the new `GameState` (via `toJsonValue`/`initFrom`), then set:
   new game id, `testing = true`, `started` = now, `finished = null`, `status = ACTIVE` once both seats are
   filled (until then `STARTING`), reset turn timer / turn start, clear dialog parameter, admin mode off.
3. Team coaches: set `teamHome.coach` / `teamAway.coach` to the assigned coaches (only inside this test game;
   real teams are never touched because nothing is uploaded). Keep the original coach names in a new
   `RecreationInfo` (see 3.6) for display ("Recreated from game #123 at command 4567, originally X vs Y").
4. Game log: copy the source log entries `1..commandNr` into the new game log, so the in-game replay of the
   recreated game shows the original history and `ClientReplayer`'s baseline algorithm stays valid
   (baseline comes from the same teams/players). New commands continue with `commandNr + 1`.
5. Push the resume sequence of the anchor (A1: ruleset `Select` generator) **only when both seats are
   joined**, then `UtilServerStartGame.startGame` resumes normally (it skips `StartGame` since the stack
   is not empty, and skips `FumbblRequestResumeGamestate` because the game is testing).
6. Register behaviours (`UtilSkillBehaviours.registerBehaviours`) and `initRulesDependentMembers` as the
   standalone start path does.

### 3.6 `RecreationInfo` (new, part of `GameState`, JSON persisted)

```
sourceGameId, sourceCommandNr, recreationKey (for link based joins),
seatHome: { coach | OPEN }, seatAway: { coach | OPEN },
initiator, createdAt
```

Persisted with the `GameState` so a server restart keeps seats. The client receives the read-only part
(source id / command nr / original coaches) with `ServerCommandJoin` or `ServerCommandGameState`
(new optional field) to show an info line and a "Recreated test game" badge.

---

## 4. Persistence and upload behaviour

Being `testing` already gives the required behaviour (see §2). Additional rules:

* `FumbblRequestCheckGamestate` must never be issued for recreated games (guaranteed because
  `started != null` → `joinWithoutTeam` resumes directly; add an explicit guard on `RecreationInfo != null`).
* `GameCache.removeGame` already deletes testing games from the DB; recreated games also get a shorter idle
  timeout (property `recreatedGameIdleMinutes`, default e.g. 60) so abandoned ones are cleaned up.
* No `ServerRequestSaveReplay` / backup of recreated games (already covered by `!isTesting()` in `StepEndGame`).
* The source game is never modified or re-cached.
* Rate limiting: max N open recreated games per initiating coach (property), to avoid abuse.
* Server property `recreateGameEnabled` (feature flag) + `ServerStatus.RECREATE_UNAVAILABLE` for clients.

---

## 5. Seats and joining

### 5.1 Seat model

Each team is a seat: `home`, `away`. A seat is either assigned to a named coach or `OPEN`
(first authenticated coach that joins for that side takes it). Everybody else joining is a spectator.
The same coach may hold both seats (practice against oneself with two clients) – allowed only in test games,
which is the case here; the join then has to be **side aware** (see 5.2) because `joinWithoutTeam` currently
derives the side from the coach name.

### 5.2 Server join handling

`ClientCommandJoin` gets two optional fields (protocol version bump):

* `recreationKey` (string) – identifies the recreated game for link based joins (works like `gameName`),
* `side` (`HOME` / `AWAY` / none = spectator for `-spectator`).

For creation via link additionally `recreateSourceGameId` and `recreateCommandNr`.

New branch in `ServerCommandHandlerJoinApproved` (or better a dedicated `RecreatedGameJoinService` called from
it, keeping the handler small):

1. Find the game by `recreationKey` (name mapping `recreate:<key>`). If absent and source/command are present,
   create it (§3.5, async load). If present but source/command differ → `ServerStatus.ERROR_UNKNOWN_GAME`.
2. `PLAYER` join: if the seat for `side` is assigned to this coach or `OPEN` → assign, set team coach, add
   session as home/away **based on `side`**, resume once both seats are connected. Otherwise
   `ERROR_GAME_IN_USE` with a message offering to spectate.
3. `SPECTATOR` join: unchanged spectator path.
4. Re-joining after a disconnect uses the normal `gameId` path (the client gets the new id with
   `ServerCommandJoin`) and works because team coach names were set.

### 5.3 Join tickets (collaborative flow)

New in-memory `RecreationTicketRegistry` (server, instance owned by `FantasyFootballServer`):

* `issue(coach, gameId, role, accountProperties)` → random 128-bit token, single use, expires after 5 minutes.
* `ServerCommandHandlerJoin`: if `password` is a valid ticket for `(coach, gameId)` → consume it and dispatch
  `InternalServerCommandJoinApproved` with the stored account properties, **without** a FUMBBL auth request.
* Only issued for sessions that authenticated (see 6.1), so a ticket never elevates an unauthenticated user.

---

## 6. Collaborative replay flow

### 6.1 Authentication of replay participants

Today `ReplaySessionManager.addSession` stores the coach name from `ClientCommandJoinReplay` unverified.
Change: when a `REPLAY` mode `ClientCommandJoin` is approved (`ServerCommandHandlerJoinApproved` REPLAY branch),
store `(session → coach, accountProperties)` in `ReplaySessionManager` as *authenticated*. On
`ClientCommandJoinReplay` the session is marked authenticated only if the names match. Unauthenticated
participants can watch and become spectators via link, but can't initiate or be seated.

### 6.2 Sequence

1. Controlling coach pauses at an anchor and clicks **"Start game from here"** (replay control bar / replay
   menu). Only the controlling coach sees the action enabled (consistent with replay control).
2. `DialogRecreateGame` (client) shows:
   * own role: *Home team (name)* / *Away team (name)* / *Spectate only*,
   * opponent: combo of authenticated participants (via the `ServerCommandJoin` coach list plus a new
     `authenticated` flag) or *Open seat – share link*,
   * if the initiator spectates: both seats selectable,
   * info: "This is a test game. Results are not uploaded and nothing is saved to your team."
3. Client sends `ClientCommandRecreateGame(commandNr, homeCoach|OPEN, awayCoach|OPEN)`.
4. Server (`ServerCommandHandlerRecreateGame`): verifies sender is controlling and authenticated, seated coaches
   are authenticated participants of the same replay session, rebuilds and validates (§3), creates the game,
   then sends each participant a personal `ServerCommandGameRecreated(newGameId, recreationKey, role, ticket)`:
   seated coaches get `PLAYER` + side, everyone else `SPECTATOR` (tickets only for authenticated sessions;
   unauthenticated spectators get a spectator link instead, see §7.3). For open seats the initiator gets the
   share link (§7).
5. Each client shows a short confirmation ("Game #… created – you play Home. Join now?" / "… join as spectator?"),
   then **relaunches in-process** (§8.2) with `-player|-spectator -gameId <new> -coach <me> -auth <ticket>
   -side <side>`. Declining keeps the replay open; the ticket expires.
6. The replay session stays alive for participants who stay; the initiating client leaves it when relaunching
   (control is passed on by existing disconnect handling).

---

## 7. Solo replay flow (no server connection)

### 7.1 Why links

The solo client closed its session and the OTP is used up; we don't want password entry. FUMBBL already turns a
click on the website (browser login = identity) into a JNLP with a fresh OTP. We reuse that: the client
produces **FUMBBL URLs**, FUMBBL produces JNLPs.

### 7.2 Client side

"Start game from here" is also available in solo replays (anchor positions only). `DialogRecreateGame` (solo
variant):

* own role: Home / Away / Spectate,
* opponent: always "share link" (optionally "restrict to coach <name>", see 7.4),
* generates a random `recreationKey` (UUID, client side, unguessable) and shows:
  * **"Launch my game"** button (opens the browser via `Desktop.browse`, fallback: copy to clipboard),
  * **Opponent link** with a copy button,
  * **Spectator link** with a copy button.

URL shape (exact path decided by FUMBBL; parameters are what FFB needs):

```
https://fumbbl.com/ffblive.jnlp?mode=player&recreate=<sourceGameId>:<commandNr>&key=<recreationKey>&side=home
https://fumbbl.com/ffblive.jnlp?mode=player&recreate=<sourceGameId>:<commandNr>&key=<recreationKey>&side=away
https://fumbbl.com/ffblive.jnlp?mode=spectator&recreate=<sourceGameId>:<commandNr>&key=<recreationKey>
```

The base URL comes from a client property (`client.url.recreate`, like other FUMBBL URLs) so standalone servers
can configure their own launcher. Links are plain text so they can be pasted in chat/Discord.

### 7.3 Required FUMBBL (outside this repository) amendments

The JNLP generator must accept `recreate`, `key`, `side`, `mode` and emit client arguments:

```
-player|-spectator -coach <logged in coach> -auth <fresh OTP>
-recreate <sourceGameId>:<commandNr> -recreationKey <key> [-side home|away]
```

Optionally the page shows a short landing text ("You are joining a recreated test game from game #…") before
offering the JNLP download. No FUMBBL-side state is required; the FFB server creates the game lazily on first
join (§5.2). The same parameters can be produced for collaborative unauthenticated spectators.

### 7.4 Optional opponent restriction

`&coach=<name>` in the opponent link; FFB server gets it as part of the creation parameters (`ClientCommandJoin`
field `reservedCoach` sent by the *initiator's* client on creation) and assigns that seat to the named coach
instead of `OPEN`. Because the initiator's link creates the game, the restriction is enforced server side,
not only by FUMBBL.

### 7.5 Ordering

Either link may be used first: the first join creates the game (from `recreate` + `key`), later joins find it by
key. The game waits (`STARTING`, `waitingForOpponent`) until both seats are connected, then pushes the resume
sequence and starts.

---

## 8. Client changes

### 8.1 Parameters

`ClientParameters`: new `-recreate <gameId>:<commandNr>`, `-recreationKey <key>`, `-side home|away`; update
`USAGE` and validation (`-recreate` requires `-player` or `-spectator`, `-coach`, `-recreationKey`).
`ClientCommunication.sendJoin` gets an overload carrying the recreation fields.
Login: `LoginLogicModule` passes them through to the join command.

### 8.2 In-process relaunch (collaborative)

`FantasyFootballClientAwt` gets `relaunch(ClientParameters)` that closes the communication, disposes the frame
and constructs a new client on the EDT (same as `main`). This avoids starting another JVM (fragile under
OpenWebStart). Fallback if relaunch proves problematic: show the solo link dialog for the participant
(the ticket is then not needed).

### 8.3 Replay UI

* Shared `RecreationAnchorService` decides if the current replay position is an anchor.
* Replay control bar / menu: "Start game from here" (enabled only at anchors, tooltip explains why not),
  "Previous anchor" / "Next anchor" navigation (`ClientReplayer` scans positions, reusing `replayTo`).
* `DialogRecreateGame` (solo and collaborative variants) and `DialogGameRecreated` (collaborative confirmation).
* In the recreated game: status bar badge "Recreated test game (from #id, cmd n)".

### 8.4 Change list

One `addFeature` entry in the latest `VersionChangeList` (`ChangeList.java`), e.g.
"Replays: start a test game from the current replay position (solo via shareable links, collaborative with
seat assignment)".

---

## 9. Protocol summary

| Command | Direction | Purpose |
|---|---|---|
| `ClientCommandJoin` (+ `recreationKey`, `side`, `recreateSourceGameId`, `recreateCommandNr`, `reservedCoach`) | C→S | Link based creation/join. |
| `ClientCommandRecreateGame(commandNr, homeSeat, awaySeat)` | C→S | Collaborative creation by the controlling coach. |
| `ServerCommandGameRecreated(gameId, recreationKey, role, side, ticket)` | S→C (per session) | Collaborative hand-over. |
| `ServerCommandJoin` (+ `authenticated` flags for replay participants) | S→C | Seat candidates in the dialog. |
| `ServerCommandGameState` / `ServerCommandJoin` (+ optional `RecreationInfo`) | S→C | Badge / info. |
| `ServerStatus.RECREATE_UNAVAILABLE`, `RECREATE_INVALID_POSITION` | S→C | Errors. |

All additions are optional JSON fields → older clients ignore them; bump protocol version for the new commands.

---

## 10. Security considerations

* The server rebuilds the state; clients only reference `(gameId, commandNr)`.
* Join tickets: single use, short lived, bound to coach + game + role, only for authenticated sessions.
* Link joins always authenticate through FUMBBL OTP (`-auth`), the key only identifies the game.
* Recreation keys are random UUIDs; creation parameters are fixed on first creation.
* Rate limits and feature flag (§4).
* Recreated games never trigger FUMBBL requests (guards in §4).

---

## 11. Implementation phases

1. **Reconstruction core**: extract baseline + reconstruction into `ReplayStateReconstructionService`
   (`ffb-common`), make `ClientReplayer` use it (no behaviour change), add `RecreationAnchorService` (A1).
   Unit tests: reconstruct a recorded game at several command numbers, compare with `ClientReplayer` result.
2. **Server creation + seats**: `RecreationSourceLoader`, `RecreatedGameFactory`, `RecreationInfo`,
   join branch with `recreationKey`/`side`, resume via `Select`. State tests in `ffb-statetest`
   (game created, both seats joined, select sequence active, testing flag, no FUMBBL requests).
3. **Solo links**: client parameters, `DialogRecreateGame` (solo), URL generation, documentation for the FUMBBL
   JNLP generator.
4. **Collaborative**: replay session authentication, `ClientCommandRecreateGame`, tickets, `ServerCommandGameRecreated`,
   in-process relaunch, dialogs.
5. **Polish**: anchor navigation, badge, change list entry, idle cleanup, rate limit.
6. **Later**: A2 setup anchor, server-only state snapshots in the log for more anchors, other rulesets.

---

## 12. Open questions

1. Should recreation be allowed from games that are still running (spectator replay of an active game)?
   Proposed: yes, the source is only read; but maybe restrict to finished games to avoid tactical scouting.
2. Turn timer in recreated games: keep source game's turn time setting or disable timers by default?
3. Should the opponent restriction (`coach=`) be the default for collaborative "share link"?
4. Is the FUMBBL OTP really single use? If not, the solo client could re-authenticate silently with the
   original `-auth` value, but relying on that would be a security smell; the link flow is preferred anyway.

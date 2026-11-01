# BB2025: Undo of Coach Actions

Status: **planned / not implemented**.
Scope: **BB2025 only**. `bb2016` and `bb2020` games never offer undo; their behaviour stays untouched.

---

## 1. Goal

The active coach can take back own decisions (moves, action selection, target selection, skill choices, ...)
as long as no information was revealed since then. Undo should not only go back to the last safe point but
to any intermediate decision between that point and now.

### 1.1 Rules (confirmed)

1. **Undo barrier.** Undo is only possible back to (and not beyond) the latest of:
   * any random event (die roll, coin toss, random player selection, card draw, shuffle, ...),
   * any decision of the opposing coach,
   * any point where the opposing coach was **prompted** for a decision (the prompt already reveals
     information, e.g. that a skill can be used),
   * any internally triggered command (timeouts, automatic actions),
   * any change of the acting team / turn (end of turn, turnover, kick-off, half time, ...).
2. **Intermediate points.** Every undoable command of the active coach after the barrier is an undo point.
   The coach may jump back to any of them, not just the most recent one.
3. **Turn timer keeps running.** An undo never restores turn time, game time or timeout state. Time spent on
   undone actions is lost.
4. **BB2025 only.** Undo is never available in `bb2016` / `bb2020` games.
5. **Replays.** Undone commands are always sent to the clients and kept in the log. Each user decides with a
   client setting whether undone actions are shown in replays. This covers standalone replays as well as
   the in-game replay of a running game. In collaborative replays the controlling coach's setting applies to
   all participants.

### 1.2 Out of scope

* Undoing actions after a random event ("mulligan") or after any opponent involvement.
* Undoing actions of the opponent, or undo requests that need the opponent's consent.
* Undo during setup, kick-off, inducement or post-game phases (can be revisited later, see §9).

---

## 2. Relevant existing infrastructure

| Concern | Location | Relevance |
|---|---|---|
| Single dice choke point | `ffb-server/.../server/DiceRoller.java` (`rollDice(int)`) | Hook for the "random event" barrier |
| Randomness bypassing `DiceRoller` | BB2025 `StepThrowARock`, `start/StepPrayers`, `end/StepAssignTouchdowns` (`Collections.shuffle`) | Must be routed through `DiceRoller` or report a barrier explicitly |
| Full server state serialization | `GameState.toJsonValue(includeLog, logLimit)` / `GameState.initFrom` | Game, step stack, current step, pass / blitz / prayer state, active effects |
| Single entry point for game commands | `ServerCommunication.handleByGameState` → `GameState.handleCommand` → `StepExecutor.handleCommand` | Hook for creating undo points and detecting opponent commands |
| Model change tracking | `ModelChange` / `ModelChangeList` | Contains **new values only**, not invertible |
| Full client resync | `ServerCommandGameState` (+ `transform()` for the away coach), `ServerCommunication.sendGameState` | Used on reconnect, reusable for undo |
| Game log / replays | `GameLog` (replayable `ServerCommand`s), `ServerReplay` (renumbers commands in `orderCommands`), `ServerReplayer`, client `ClientReplayer` | Server marks undone entries; hiding happens in `ClientReplayer` only |
| Game log persistence | `DbGamesSerializedUpdateParameter` (whole `GameState` including the log as one gzipped blob) | In-place marks of log entries are persisted by the next update |
| Collaborative replays | `ReplayState`, `ReplaySessionManager.hasControl`, `ServerCommandReplayStatus` (syncs by list position), prevent-sketching commands and handlers | Template for syncing the controller's visibility setting |
| Turn timer | `UtilServerTimer` (`syncTime`, `startTurnTimer`, `stopTurnTimer`), `Game.turnTime/gameTime`, transient `GameState.turnTimeStarted` | Must be preserved across restore |
| Game options | `GameOptionId`, `GameOptionFactory`, `UtilGameOption.isOptionEnabled` | New option for enabling undo |
| Client settings | `CommonProperty`, `IClientPropertyValue`, `ClientSettingsMenu`, `ReplayMenu` | New setting for showing undone actions in replays |

---

## 3. Options

### Option A: Snapshot before each undoable command (recommended)

Before an undoable command of the active coach is processed, serialize `GameState` (without game log) and
store it together with a label and the current command number. Undo restores the selected snapshot and sends
a full game state to all clients.

Pros
* Reuses the existing, production-tested persistence (`toJsonValue` / `initFrom`).
* Intermediate undo points are trivial: one snapshot per point.
* Restoring is O(1) per undo, independent of how far back.
* Engine and steps stay unchanged.
* Every serialization gap that gets fixed also improves restart / reload robustness.

Cons
* Any step field not serialized becomes an undo bug. Today such gaps only surface after a server restart,
  undo surfaces them constantly.
* Memory: a snapshot is a few hundred KB of JSON. Mitigated because the stack is cleared at every barrier
  (typically a handful of entries) and can be capped.
* The client gets a full game state instead of incremental changes, so no animations for the undo itself.

### Option B: Single snapshot at the barrier + deterministic re-execution

Store only one snapshot at the last barrier plus the list of active-coach commands since then. To undo to
point *k*, restore the snapshot and re-run commands `1..k` in a silent mode (no sends, no game log, no DB,
no timers). A die roll during re-execution is an error, because the barrier guarantees there is none.

Pros
* Only one snapshot per barrier.
* Arbitrary intermediate points.
* Built-in determinism check of the step engine.

Cons
* Still requires Option A's serialization completeness for the barrier snapshot.
* A silent execution mode touches communication, logging, DB updates, timers and player markings. This is
  the riskiest part of the whole feature.
* Undo cost grows with the number of commands since the barrier.
* Hidden side effects (e.g. `queueDbUpdate` inside steps, `updatePlayerMarkings`) must all be suppressed.

### Option C: Invertible model changes

Extend `ModelChange` with the old value and apply recorded change lists in reverse.

Pros
* Incremental client updates, so the undo could even be animated.
* Small memory footprint.

Cons
* Covers the `Game` model only. Step stack, current step, `PassState`, `BlitzTurnState`, `ActiveEffects`,
  zapped players etc. are not model changes and would still need snapshots (i.e. Option A anyway).
* Every setter in `ffb-common` would have to record old values correctly. This is a huge and very
  error-prone change for little extra benefit.
* Order-sensitive collection changes (add/remove in lists and maps) are hard to invert exactly.

### Option D: Client-side tentative actions ("plan, then confirm")

Let the client stage actions locally (e.g. a planned movement path) and only send them on confirmation.

Pros
* Zero server risk.
* Instant feedback.

Cons
* Not a real undo: anything already sent cannot be taken back.
* Needs per-action client logic. Only covers actions that have no server-side effects before confirmation.
* Can be a complementary UX improvement (movement path planning) but does not meet the requirement.

### Decision

**Option A** is implemented. Option B stays possible later as a memory optimization, built on top of A's
serialization work. Option C is rejected. Option D may be done independently as a UX feature.

---

## 4. Design (Option A)

### 4.1 Undo history service (server)

New instantiable service `ffb-server/.../server/undo/bb2025/UndoHistory` owned by `GameState`.
The undo points are transient and not serialized; they are lost on a server restart, which is acceptable.
Only the id counter is serialized with the `GameState` (outside the snapshots, see §5.2).

```
UndoPoint { int id; String label; String teamId; JsonObject snapshot; }

UndoHistory
  UndoPoint recordPoint(String label, String teamId, JsonObject snapshot)  // assigns the next id
  void barrier(UndoBarrierReason reason)   // clears all points (the id counter keeps increasing)
  List<UndoPoint> points()
  UndoPoint pointFor(int id)
  Integer currentPointId()                 // id of the newest existing point, null if none; used for tagging
  void dropFrom(int id)                    // removes the point and all newer points
```

* Cap the number of points (e.g. 50). When the cap is exceeded the oldest point is dropped, which simply
  moves the effective barrier forward.
* `UndoBarrierReason` (enum, used for debug logging): `DICE`, `SHUFFLE`, `OPPONENT_COMMAND`,
  `OPPONENT_PROMPT`, `INTERNAL_COMMAND`, `TURN_CHANGE`, `PHASE`, `CAP`.

### 4.2 Creating undo points

Hook in `StepExecutor.handleCommand` (or a thin wrapper in `GameState.handleCommand`), before the step
handles the command:

1. Undo is active for the game (BB2025 rules and the game option is enabled, see §6).
2. The command was sent by the coach of the team that is currently active (`game.isHomePlaying()`).
3. The command is **undoable** (see below).
4. The game is in a phase where undo is allowed (regular turn, see §1.2).

If all hold: `UndoHistory.recordPoint(label, teamId, gameState.toJsonValue(false, 0))`. All commands sent
while handling this command are then tagged with the new point's id (§5.2).

**Undoable commands.** Add `boolean isUndoable()` to `ClientCommand` (default `false`) and enable it for
the commands that make game decisions, e.g. acting player selection, move, block, blitz / foul / pass / hand-off
target selection, skill use, re-roll / modifier choices, end player action, throw team mate / kick team mate
target selection. Chat, ping, markings, settings, concession, illegal procedure etc. stay `false` and
never create points.

**Labels.** Derived on the server from the command and the acting player, e.g. "Move Lineman #4",
"Select Blitz: Blitzer #2", "Use Sidestep". A small label provider per command type keeps this out of steps.

### 4.3 Barriers

* **Dice.** `DiceRoller.rollDice(int)` calls `gameState.getUndoHistory().barrier(DICE)`. All public roll
  methods funnel into it, so one hook suffices. Test rolls (`addTestRoll`) also count.
* **Shuffles.** Replace `Collections.shuffle` in the BB2025 steps with a new `DiceRoller.shuffle(List)`
  that uses Fortuna and raises the barrier. If touching those steps is undesired, call
  `barrier(SHUFFLE)` next to the existing shuffle instead.
* **Opponent command.** Any game command from the coach of the non-active team (or any undoable command whose
  sender is not the active coach).
* **Opponent prompt.** Raised when a dialog is opened for the opponent. Hook: the model change
  `GAME_SET_DIALOG_PARAMETER` observed in `GameState.update` combined with a check whether the dialog is
  addressed to the opponent (`DialogParameter` team / coach information). Fallback: steps that wait for the
  opponent without a dialog (e.g. block die choice by the defender) must call `barrier(OPPONENT_PROMPT)`
  explicitly. Collect these cases in a checklist during implementation.
* **Internal commands.** Any `InternalServerCommand` that reaches `GameState.handleCommand`, except
  `InternalServerCommandApplyAutomatedPlayerMarkings`, which has no game effect.
* **Turn change.** Changes of `turnData`/`homePlaying`/turn number observed via model changes
  (`GAME_SET_HOME_PLAYING`, `TURN_DATA_SET_TURN_NR`, ...) or raised from BB2025 `EndTurn` sequence steps.
* **Phase.** Leaving `TurnMode.REGULAR` (and the BB2025 equivalents for blitz turn etc., to be decided per
  mode) raises a barrier.

### 4.4 Performing an undo

New client command `ClientCommandUndo(int undoPointId)` with handler `ServerCommandHandlerUndo`, processed
through the regular game command path:

1. Validate: BB2025 and option enabled, sender is the active coach, point exists and belongs to that team,
   game is currently waiting for a command from that coach (`StepExecutor` idle, no pending dialog for the
   opponent).
2. `UtilServerTimer.syncTime(gameState, now)` and capture: `game.getTurnTime()`, `game.getGameTime()`,
   `game.isTimeoutPossible()`, `game.isTimeoutEnforced()`, `game.isWaitingForOpponent()` (if relevant) and
   the transient `gameState.getTurnTimeStarted()`.
3. Restore: `gameState.initFrom(source, point.snapshot)` with the **current** game log preserved (the snapshot
   does not contain the log; `initFrom` must not clear it, add a parameter or a dedicated
   `restoreSnapshot` method that skips the log). Re-register observers, rebuild `StepFactory`.
4. Re-apply the captured timer values (rule §1.1.3). `turnTimeStarted` stays as is.
5. `GameLog.markUndone(point.id)` (§5.2), then `UndoHistory.dropFrom(point.id)`. The restored state equals
   the state *before* that command, so the point itself is consumed.
6. Add a report `ReportUndo(coach, undoneCount, label)` so the action is visible in the log for both
   coaches and spectators.
7. Send `ServerCommandUndo` (see §5) to all sessions (away coach gets the transformed game), queue
   `GameCache.queueDbUpdate(gameState, true)` and call `gameState.updatePlayerMarkings()`.

### 4.5 Exposing undo points to the client

Clients need the list of points for the UI. Options:

* **(preferred)** A lightweight model field `Game.undoPoints` (list of `{id, label}` for the active team) with
  a `ModelChangeId` so it travels with the regular model sync. Only id and label, never the snapshot.
* A separate non-replayable server command after each change. Simpler but adds a parallel channel.

### 4.6 Client

* Undo button / `Ctrl+Z` for the last point, plus a menu or popup "Undo to ..." listing all points.
* Only enabled for the active coach in BB2025 games with undo enabled and when points exist.
* `ClientCommandHandlerUndo` replaces the game like `ClientCommandHandlerGameState`, resets the client state
  (`ClientStateFactory` recomputes the state from the restored game) and refreshes the UI.
* The in-game client replayer (`ClientReplayer`) must handle `ServerCommandUndo` when replaying the local log.

---

## 5. Game log and replays

### 5.1 Logging

New replayable `ServerCommandUndo(int undoPointId, Game game)`:

* `undoPointId` is the id of the restored point.
* `game` is the full restored game (with `transform()` for the away coach, like `ServerCommandGameState`).
* Replaying it on any client means: replace the current game with `game`.

Command numbers can **not** identify undone commands. `ServerReplay.orderCommands()` renumbers all log
entries to `i + 1` before a replay is sent, and talk commands are logged without a number. Command numbers
therefore differ between live play, reconnects and replays.

### 5.2 Identifying undone commands

Undone commands are identified by an **undo point tag**. The server also **marks them in the game log**, so
replays carry the complete information.

**Undo point ids.**
* Every undo point gets an id from a per-game counter that only ever increases.
* The counter is stored in the serialized `GameState`, so ids are never reused after a server restart.
* The counter is **not** part of the undo snapshot. Restoring a snapshot must not reset it, otherwise ids
  would be reused.

**Tagging (when sent).**
* `ServerCommand` gets an optional `undoPointId` field. It is omitted from the JSON when not set, so old
  logs and replays still load.
* Every logged game command sent by `ServerCommunication` is stamped with the id of the newest existing
  undo point (`UndoHistory.currentPointId()`), or not stamped if there is none.
* The away coach's copies created by `transform()` (`ServerCommandModelSync`, `ServerCommandGameState`)
  copy the tag.
* Talk commands are never tagged.
* All commands caused by the command at point P carry tag `>= P`, because the point is recorded before the
  command is handled.

**Marking (when undone).**
* Undoing to point P sets `undone = true` on every `GameLog` entry with `undoPointId >= P` that is not
  already marked.
  * Only entries after point P can carry such a tag, because ids only ever increase and later points are
    created after the undo.
  * The marking runs inside the existing `synchronized` block of `GameLog` (new method
    `GameLog.markUndone(int undoPointId)`).
* `undone` is a second optional `ServerCommand` field, serialized only when `true`.
* The entries are updated in place. Each database update stores the whole `GameState` including the log
  as one blob (`DbGamesSerializedUpdateParameter`), and the undo queues such an update. The marks are
  therefore persisted without any extra database work.
* There is precedent for changing log entries in place: `ServerReplay.orderCommands()` already overwrites
  command numbers on the shared log objects.
* `ServerCommandUndo` itself is stamped like any other command. After an undo to point P, the newest
  remaining point is older than P, so the `ServerCommandUndo` carries that point's tag.
* **Nested undos** need no special handling: a later undo to an even older point also marks the earlier
  `ServerCommandUndo` and everything around it as undone.

**Consequences.**
* Replays from the server (reconnect, `ServerReplay` / `ServerReplayer`, saved replay, FUMBBL upload) carry
  the `undone` marks. The client knows whether a command is undone the moment it reaches it, without looking
  ahead.
* Live clients received the commands before they were marked. For the in-game replay they mark their own
  copies when `ServerCommandUndo` arrives (§5.4).
* A replay being streamed while an undo happens may send part of the range unmarked. This is harmless,
  because a replay client marks its local copies when it receives the `ServerCommandUndo`, just like a
  live client (§5.4).

### 5.3 Undone commands always reach the client

The server never filters undone commands. The live game, the reconnect replay, `ServerReplay` /
`ServerReplayer`, the saved replay and the FUMBBL upload always contain the complete log, including undone
commands (marked, §5.2) and `ServerCommandUndo`. Whether undone actions are shown is decided only on the
client.

This keeps the server simple: there is one log format and no server-side filter. Any client can switch the
view at any time without reloading the replay.

### 5.4 Client-side visibility setting

New client setting (user property, not a game option) `CommonProperty.SETTING_REPLAY_SHOW_UNDONE_ACTIONS`
with values `IClientPropertyValue.SETTING_REPLAY_UNDONE_SHOW` / `SETTING_REPLAY_UNDONE_HIDE`. It is shown
in the settings menu, and as a toggle in `ReplayMenu` for quick access. The default is still open (§10).

* **Shown:** commands are replayed as received. Viewers see the undone actions, then `ServerCommandUndo`
  resets the game to the restored state.
* **Hidden:** the client skips every command marked `undone` and every `ServerCommandUndo`. The restored
  state equals the state before the undone commands, so the remaining commands replay consistently.
  * Talk commands (`ServerCommandTalk`) are never tagged or marked, so chat is not lost.
  * `ReportUndo` is kept as a hint ("Coach X took back an action"), possibly decided per setting (§10).
  * Timer values in later model syncs already include time spent on undone actions, which is correct.

**Local marking on the client.** When a client receives a `ServerCommandUndo(P)` (live, or while a replay is
streaming), it sets `undone = true` on all commands in its replay list (`ClientReplayer.fReplayList` and
`fUnseenList`) with `undoPointId >= P`. This is the same rule the server applies. Afterwards the client's list
matches the server's marked log.

**Implementation in `ClientReplayer`.**

* An instantiable `UndoneCommandVisibility` service answers `isHidden(ServerCommand, boolean showUndone)`
  and `nextVisible(int position, boolean forward)` from the `undone` flags. It needs no index and no look
  ahead.
* The replay list (`fReplayList` / replay commands) is **never** filtered. Positions stay identical on all
  clients regardless of the setting. This matters because collaborative replays synchronise by list position
  (`ServerCommandReplayStatus.getCommandNr()` is passed to `replayTo(int pReplayPosition)`).
* `replayTo` skips hidden positions when the setting is "hide". Stepping, playing and skipping use
  `nextVisible` so hidden commands are never shown and take no playback time. Jumping to a hidden position
  (e.g. from `replayToCommand` or a status received from the controller) moves to the next visible position in
  the current direction.
* Changing the setting while a replay is open re-runs `replayTo` from 0 to the current position (as is
  already done for backward jumps). If the current position becomes hidden, it moves to the next visible
  position.
* **In-game replay.** The setting also applies to the in-game replay of a running game
  (`ClientMode.PLAYER` / `SPECTATOR`, entering replay mode during the game):
  * `ClientReplayer` uses the same `UndoneCommandVisibility` and skipping there.
  * The commands in the in-game list were received live before they were marked, so the local marking
    described above is what makes hiding work there.
  * Leaving the in-game replay returns to the live state as today (`positionOnLastCommand`). That state
    already includes all undos, so it is independent of the setting.
  * In-game replays are never collaborative, so the local setting always applies there.
* **Live play is not affected.** During a running game every client always applies every command as it
  arrives. The setting only affects how the log is played back.

### 5.5 Collaborative replays

In a collaborative replay (`ReplayState`, `ReplaySessionManager`) the **controlling coach's** setting
applies to all clients in the session. A viewer's own setting is ignored while they are not in control. This
follows the existing prevent-sketching flow
(`ClientCommandSetPreventSketching` → `ServerCommandHandlerSetPreventSketching` →
`ServerCommandSetPreventSketching`).

* `ReplayState` gets a `showUndoneActions` field.
* New `ClientCommandSetShowUndoneActions(boolean)`. It is accepted only from the session with control
  (`sessionManager.hasControl(session)`). The handler stores the value in `ReplayState` and sends
  `ServerCommandSetShowUndoneActions(boolean)` to all sessions of the replay.
* The controller's client sends its setting:
  * when it creates or joins the replay as controller,
  * when it receives control (`ServerCommandReplayControl` naming its own coach),
  * whenever it changes the setting locally.
* `ServerCommandHandlerJoinReplay` sends the current `ReplayState.showUndoneActions` to a joining session,
  next to the replay status and prevent-sketching commands.
* `ServerCommandHandlerTransferControl`: after the control change the new controller's client sends its
  setting. Until it arrives the previous value stays active, so there is no flicker.
* On the client, `ClientReplayer` keeps an `effectiveShowUndoneActions`:
  * offline replays, or online replays where this client has control: the local setting;
  * online replays where this client has no control: the last value received from the server.
* While not in control, the toggle in `ReplayMenu` is disabled or shown read-only with a hint that the
  controlling coach decides. Changing the local setting is still stored and becomes effective once control
  is received.
* Synchronised positions are never ambiguous, because all clients share the same unfiltered list and the
  same effective setting. A received position that is hidden is moved to the next visible position by each
  client in the same deterministic way.

---

## 6. Enabling undo

* New game option `GameOptionId.ALLOW_UNDO` (boolean, default to be decided) so leagues and tournaments can
  opt in or out. Optional `GameOptionId.UNDO_MAX_PER_TURN` (int, `0` = unlimited) if abuse is a concern.
* Undo is active only if `game.getRules() == RulesCollection.Rules.BB2025` **and** `ALLOW_UNDO` is
  enabled. The check lives in one place (e.g. `UndoAvailability` service) used by server and client.
* Register the options in `GameOptionFactory` with user-facing messages, and in `UtilServerStartGame` where
  BB2025 defaults are set.

---

## 7. Serialization completeness (prerequisite)

Undo exposes every step field that is missing from `IStep.toJsonValue` / `initFrom`. Before enabling:

1. **Round-trip test mode in `ffb-statetest`:** after every handled command, serialize the full `GameState`,
   restore it into a fresh instance and continue the test with the restored one. Run the whole BB2025 test
   suite in this mode. Any failure points to a missing field.
2. Audit BB2025 steps (`ffb-server/.../step/bb2025/**`) and BB2025 `mixed`/`common` steps used in BB2025
   sequences for non-transient fields not covered by serialization. Pay special attention to the step state
   in the agility modifier steps (`roll`, `reRollUsed`, `awaitingRescue`, `selectedModifierSkills`).
3. Server-side transient state outside `GameState.toJsonValue` that affects game flow must be reviewed
   (e.g. `DiceRoller` test rolls are irrelevant for production, `ReRollService` is stateless).

---

## 8. Implementation phases

1. **Serialization hardening.** Round-trip test mode in `ffb-statetest` (§7), fix gaps. Valuable on its own.
2. **Server core.** `UndoHistory`, barriers (§4.3), `ClientCommand.isUndoable`, point creation (§4.2),
   game options (§6), `ClientCommandUndo` + handler, restore with timer preservation (§4.4), `ReportUndo`,
   `ServerCommandUndo`, `undoPointId` tagging and `undone` marking of log entries (§5.2).
3. **Client.** Undo points in the model (§4.5), handler for `ServerCommandUndo` including local marking,
   UI (button, shortcut, "undo to" list), report rendering.
4. **Replays.** `SETTING_REPLAY_SHOW_UNDONE_ACTIONS` client setting, `UndoneCommandVisibility` and skipping
   in `ClientReplayer` (§5.4), collaborative sync of the controller's setting (§5.5).
5. **Change list.** Add a feature entry to the latest `VersionChangeList` in `ChangeList.java`
   (no rules version mentioned, BB2025 is the default).

---

## 9. Testing

`ffb-statetest` (`GameStateBuilder` / `StepEngine` / `Commands` / `TestRolls`):

* Move several squares without dodge/rush, undo to each intermediate point, state equals the snapshot.
* A dodge roll creates a barrier: no point before the roll remains.
* Opponent prompt (e.g. defender chooses block die, opponent skill dialog) creates a barrier.
* Undo is rejected for the non-active coach, for unknown point ids and for non-BB2025 games.
* Undo is rejected while waiting for the opponent.
* Turn time and game time after undo are not lower than before the undo.
* The game log keeps all undone commands and `ServerCommandUndo` (no server-side filtering).
* Commands caused by an undone action carry `undoPointId >= P` and are marked `undone`. Earlier commands and
  talk commands are not marked.
* A nested undo (undo, more actions, undo to an older point) also marks the first `ServerCommandUndo`.
* After a simulated restart (serialize / restore `GameState`), new undo points get ids higher than all
  existing ones, and the `undone` marks survive.
* Full BB2025 suite in serialization round-trip mode (§7).

Unit tests:
* `UndoHistory`: cap, `dropFrom`, barrier, ids keep increasing across barriers and restores.
* `GameLog.markUndone`, plus JSON round-trip of `undoPointId` / `undone` on `ServerCommand`. Commands
  without these fields (old logs) still load.
* Away coach copies created by `transform()` keep `undoPointId`.
* `UndoneCommandVisibility`:
  * `isHidden` / `nextVisible` in both directions, including at the list boundaries;
  * talk commands are never hidden.
* `ClientReplayer` (client-logic):
  * hide mode reaches the same final game state as show mode;
  * toggling the setting mid-replay keeps a valid position;
  * in-game replay: a live `ServerCommandUndo` marks the local copies, which are then hidden.
* Collaborative replay handlers:
  * the setting is accepted only from the controlling session;
  * it is broadcast to all sessions;
  * it is sent on join;
  * the new controller's value applies after a control transfer.

---

## 10. Open questions

1. Default of the `ALLOW_UNDO` game option and of the `SETTING_REPLAY_SHOW_UNDONE_ACTIONS` client setting.
2. Keep or remove `ReportUndo` in replays with hidden undone actions.
3. Limit per turn (`UNDO_MAX_PER_TURN`) needed?
4. Undo during setup / blitz kick-off turn / other special turn modes in a later iteration?
5. Should undo points survive a server restart (persist `UndoHistory` with the serialized game)?

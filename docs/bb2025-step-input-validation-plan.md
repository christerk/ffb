# BB2025: Server-Side Validation of Client Input in Steps

Status: **planned / not implemented**.
Scope: **BB2025 only** for steps. `bb2016` and `bb2020` keep their current behaviour (see §3.1).
The command handlers in WP12 are not ruleset specific and are fixed for all rulesets.
Basis: audit of all server steps that accept client commands, done by code reading at commit `4a0f9d4`,
followed by a read of the non-step command handlers (`handler/`) and the socket layer.
Line numbers refer to that commit. Paths are relative to
`ffb-server/src/main/java/com/fumbbl/ffb/server` unless they start with `ffb-`.

---

## 1. Goal

Every step must accept a client command only if

1. it comes from the coach who is entitled to the decision,
2. the step is waiting for exactly that answer, and
3. every value in it is one of the options the rules (and the server's own computation) allow.

Anything else is ignored without changing game state and without throwing.

## 2. Findings in short

- Only the two coaches' sessions reach a step (`net/ServerCommunication.java:185-190`). After that there
  is no filter: the current step gets every command from either coach (`StepExecutor.java:108-113`).
- An exception while handling a command shuts the match down (`ServerCommunication.java:192-195`).
  All games share one command thread (`ServerCommunication.java:102-112`).
- Of 113 BB2025 steps that accept client input, 107 have at least one gap. Move paths, re-roll
  *sources* and the modifier-choice dialogs are validated; most other input is trusted.

The gaps fall into nine root causes. The work packages in §6 are grouped by game area, but most rows
are instances of one of these:

| # | Root cause | Fixed in |
|---|---|---|
| R1 | Shared handlers (`AbstractStepWithReRoll`, `AbstractStepMultiple`, `AbstractStep.handleSkillCommand`) never check the sender | WP0 |
| R2 | `CLIENT_PLAYER_CHOICE` with mode `LORD_OF_CHAOS` re-runs any re-roll step, for any sender, at any time | WP0 |
| R3 | Steps re-execute on any accepted command without remembering that the roll was already made | per step |
| R4 | Player ids and squares are not compared with the option list the server computed for the dialog | WP0 service + per step |
| R5 | Action declaration (`CLIENT_ACTING_PLAYER`) and action targets are validated only in the client | WP2 |
| R6 | `RollMechanic.useReRoll` does not check skill ownership for skills tracked outside the activation | WP0 |
| R7 | Setup helpers check neither player state, own half, sender nor team | WP9 |
| R8 | Null / unknown values are dereferenced (match shutdown) | WP0 + WP11 |
| R9 | `CLIENT_CONCEDE_GAME` is not tied to sender or dialog | WP0 |

**Verification status.** Nothing was run against a server. Rows marked ✔ had their decisive lines re-read
during consolidation of the audit; all other rows are as reported by the audit and still need to be
confirmed. Therefore every fix starts with a test that reproduces the gap (§7). If a gap cannot be
reproduced, note that in the PR and drop the row.

---

## 3. Constraints

### 3.1 Ruleset scope

Behaviour changes apply to BB2025 only. Per `AGENTS.md`:

- **Classes in `step/bb2025`, `skillbehaviour/bb2025`, `mechanic/bb2025`, `util/bb2025`**: change in place.
- **Concrete steps and behaviours shared with other rulesets**: split before changing (decided). The class is
  duplicated into each ruleset package and only the `bb2025` copy gets the fix. No inheritance between
  ruleset classes. Affected classes:

  | Shared today | Classes |
  |---|---|
  | `COMMON` (`step/action`, `step/phase`) | `StepDivingTackle`, `StepDauntless`, `StepDumpOff`, `StepJuggernaut`, `StepWrestle`, `StepCoinChoice`, `StepReceiveChoice` |
  | `mixed` (BB2020 + BB2025) steps | `StepTentacles`, `StepInitPassing`, `StepAnimalSavagery`, `StepPickMeUp`, `StepUnchannelledFury`, `StepThrowKeg`, `StepQuickBite`, `StepFirstMoveFuriousOutburst`, `StepDoubleStrength`, `StepDauntlessMultiple`, `StepFoulAppearanceMultiple`, `AbstractStepMultiple` |
  | `mixed` behaviours | `IndomitableBehaviour`, `BlindRageBehaviour` |

  When a step is duplicated, the BB2025 behaviours that hook into it must be re-pointed to the new class:
  `StepModifier.appliesTo` is a class-equality check, and the modifier's generic type and `StepState`
  type name the step class.
- **Base classes that cannot be split** (`AbstractStep`, `AbstractStepWithReRoll`): delegate the decision
  to a ruleset mechanic whose `bb2016` / `bb2020` implementations keep today's behaviour. This is the
  existing pattern: `AbstractStep.isReRollSourceOffered` → `RollMechanic.isReRollSourceOffered`, which
  returns `true` for `bb2016` and `bb2020`.
- **Shared utilities** (`util/UtilServerSetup`, `AbstractStep.setPlayerCoordinates`): leave untouched;
  BB2025 steps call a new `util/bb2025` service instead.

### 3.2 Behaviour of rejected commands

- A rejected command returns `UNHANDLED_COMMAND`, changes nothing and does not call `executeStep()`.
- No exceptions for bad input. Existing `throw new FantasyFootballException` on client values
  (e.g. `StepApplyKickoffResult:202-204`, `StepBuyInducements:178-182`) becomes "ignore".
- Where the client may have changed its local view optimistically (setup moves), re-send the server
  state as `AbstractStep.handleSetupCommand` and `UtilServerSetup.setupPlayer` already do with
  `fieldModel.sendPosition(player)`.
- The sender gets no feedback (decided). Log each rejection once at **warn** level with game id, coach,
  step id, command id and reason, so attempts are visible in the server log.

### 3.3 The official client must keep working

Every rule that is added on the server already exists in the client. The server check must accept
everything the unmodified client can legitimately send, including the commands it sends to switch the
action of the current player. Before tightening a branch, read the matching client logic module
(`ffb-client-logic/.../client/state/logic`) and mirror it. Where the rule already lives in `ffb-common`
(`UtilPlayer.findAdjacentBlockablePlayers`, `UtilPlayer.findEligibleDivingTacklers`, `TtmMechanic`,
`GameMechanic.isWisdomAvailable`, `ApothecaryType.forPlayer`, `InducementType.availability`), call that
instead of re-implementing it.

### 3.4 Persistence

Steps are serialised by `StepId`, so moving a step class to another package is safe for stored games.
New step state needs an `IServerJsonOption` entry and must read as "not set" from games stored before
the change.

### 3.5 Code conventions

New logic goes into instantiable services with instance methods, created at the call site. No `f` / `p`
prefixes on new fields and parameters. Imports as described in `AGENTS.md`.

---

## 4. Design

### 4.1 Rule for every command branch

> A branch may return `EXECUTE_STEP` only when the step is waiting for exactly that answer.

"Waiting" is decided from the dialog stored in `game.getDialogParameter()` or from explicit step state,
never from the command itself.

### 4.2 Building blocks (WP0)

| Building block | Location | Purpose |
|---|---|---|
| `CommandValidationMechanic` | `mechanic/` + `bb2016`, `bb2020`, `bb2025`; new `Mechanic.Type.COMMAND_VALIDATION` in `ffb-common` | Lets the unsplittable base classes ask the ruleset whether a command is acceptable. `bb2016` / `bb2020` accept everything (today's behaviour). |
| `DialogAddresseeService` | `util/bb2025` | Resolves the team entitled to answer the current dialog from its `teamId`, `choosingTeamId` or `playerId`, and tells whether a `ReceivedCommand` comes from that team's coach. Returns "unknown" for dialogs that carry none of these; those steps check the sender explicitly with the `UtilServerSteps.checkCommandIsFrom*` helpers. |
| `OfferedPlayerChoiceService` | `util/bb2025` | Accepts a `CLIENT_PLAYER_CHOICE` only if the current dialog is a `DialogPlayerChoiceParameter` with the expected `PlayerChoiceMode`, the sender is the coach of its `teamId`, and the selected ids are distinct, a subset of `getPlayerIds()` and within `getMinSelects()` / `getMaxSelects()`. Returns the accepted ids. |
| `OfferedSkillService` (exists) | `util/` | Already tells whether a skill is offered by the current dialog and to which player. Used for every `CLIENT_USE_SKILL` answer. |

### 4.3 Sender, expectation and value in one place per step

Where a decision needs game rules (legal block target, legal action, legal setup square) the rule goes
into a small `util/bb2025` service so it can be unit tested without a step. The step only wires
command → service → state.

---

## 5. Order of work

1. **WP1** (server-wide hang) and **12.1** (a spectator can shut down a match), each on its own, first.
2. **WP0** (foundations). Removes R1, R2, R6, R9 and most of R8 for every step at once.
3. **WP2, WP4, WP9, WP10** (largest advantage for a cheating coach).
4. **WP3, WP5, WP6, WP7, WP8.**
5. **WP11** (remaining crash-only inputs) and the rest of **WP12** (command handlers).

One pull request per work package; WP2, WP4, WP6 and WP9 can be cut further along their rows.

---

## 6. Work packages

Columns: **Sev** = C(ritical) / H(igh: advantage or opponent's decision) / M(edium: state corruption,
limited benefit) / L(ow: crash-only or only hurts the sender).

### WP1 — Kickoff coordinate hangs the server

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 1.1 ✔ | C | `step/bb2025/kickoff/StepKickoff:55-62` | `ballCoordinate` is stored without sender, null, bounds or half check. | Accept only from the kicking coach (`checkCommandIsFromCurrentPlayer`), non-null, inside the receiving half (`HALF_AWAY` if home is playing, else `HALF_HOME`). |
| 1.2 ✔ | C | `step/bb2025/kickoff/StepKickoffScatterRoll:145-148`, `StepKickoffScatterRollAskAfter:152-155` | `while (!FIELD.isInBounds(lastValidCoordinate)) { … --distance }` never ends for a start point far off the pitch; it runs on the single command thread. | Bound both loops (`distance > 0`); if no valid coordinate is found treat the kick as a touchback. Keep this even with 1.1 in place. |

### WP0 — Foundations

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 0.1 ✔ | H | `step/AbstractStepWithReRoll:44-79` | `CLIENT_USE_RE_ROLL` and the single-die re-roll skill are accepted from either coach. A `null` source ("no re-roll") is always offered (`util/bb2025/OfferedReRollSourceService:86-88`), so the opponent can decline; with a real source he spends the owner's re-roll. | Before the existing checks ask `CommandValidationMechanic` whether the sender is the dialog's addressee (`DialogAddresseeService`). |
| 0.2 ✔ | H | `step/AbstractStepWithReRoll:58-63` | `CLIENT_PLAYER_CHOICE` / `LORD_OF_CHAOS` returns `EXECUTE_STEP` unconditionally. Effects per step are listed in §6.1. | Ask the mechanic. BB2025 never shows this dialog (`grantsSingleUseTeamRerollWhenOnPitch` is only registered by `skill/bb2020/special/LordOfChaos`), so the BB2025 mechanic rejects the command. |
| 0.3 | H | `step/mixed/multiblock/AbstractStepMultiple:41-46`, `StepDauntlessMultiple:77-86`, `StepFoulAppearanceMultiple:78-86` | Same as 0.1 / 0.2 for the multi-block steps: re-roll for target without sender check, unconditional `LORD_OF_CHAOS`. | Split (§3.1). BB2025 copies: sender must be the acting coach; no `LORD_OF_CHAOS` branch. `step/bb2025/mutliblock/StepBlockRollMultiple` and `step/bb2020/multiblock/StepBlockRollMultiple` extend `AbstractStepMultiple` today and move to the copy of their own ruleset. |
| 0.4 ✔ | H | `step/AbstractStep:147-173` (`handleSkillCommand`) | Dispatches on the skill named in the command. No sender, no "was this offered", no ownership check; the method does not even receive the `ReceivedCommand`. | Add `handleSkillCommand(ReceivedCommand, state)`. BB2025 mechanic requires `OfferedSkillService.isOffered(game, skill)` and the sender to be the coach of `offeringPlayerId`. Migrate all BB2025 callers; remove the old overload once unused. |
| 0.5 ✔ | L | `step/AbstractStep:151-152` | `usedSkill.getSkillBehaviour()` is `null` for skills without a server behaviour (e.g. Block); `GameState.executeStepHooks:519-520` has the null check, this method does not. | Return `UNHANDLED_COMMAND` if the behaviour is `null`. |
| 0.6 ✔ | H | `mechanic/bb2025/RollMechanic:311-312` | For skills tracked outside the activation `useReRoll` returns `!pPlayer.isUsed(skill)`; a player who does not own the skill passes. | Require `UtilCards.hasSkill(player, skill)` as well. |
| 0.7 ✔ | M | `step/AbstractStep:247-288` (`handleConcedeGame`) | `DENIED` calls `hideDialog` for whatever dialog is pending, from either coach, in every step. `CONFIRMED` needs neither a prior `REQUESTED` nor `isConcessionPossible()`. | Ask the mechanic. BB2025: handle `CONFIRMED` / `DENIED` only while `DialogConcedeGameParameter` is the current dialog and the sender is the coach who requested it. |
| 0.8 | — | new | — | `DialogAddresseeService`, `OfferedPlayerChoiceService` (§4.2) with unit tests. |

#### 6.1 Steps to regression-test after 0.1 / 0.2

Each step below changed behaviour when re-run through the unconditional `LORD_OF_CHAOS` branch. After
WP0 the command must have no effect in any of them.

| Effect before the fix | Steps |
|---|---|
| Rolls again for free, unlimited | `StepBlockRoll` ✔, `StepBlockChainsaw`, `StepProjectileVomit`, `StepChomp`, `StepFoulChainsaw`, `StepHypnoticGaze`, `StepStandUp`, `StepTrapDoor`, `StepDauntless` ✔, `StepAnimosity`, `StepFoulAppearance`, `StepBalefulHex`, `StepLookIntoMyEyes`, `StepAllYouCanEat`, `StepInitScatterPlayer`, `StepGettingEven`, `StepShadowing`, `StepTentacles`, `StepBribes`, `StepBlockRollMultiple` |
| Failed roll treated as passed | `StepBoneHead` ✔, `StepReallyStupid`, `StepTakeRoot`, `StepUnchannelledFury`, `StepBloodLust`, `StepAnimalSavagery` |
| Re-roll declined / action fails | `StepGoForIt`, `StepJump`, `StepMoveDodge`, `StepPickUp`, `StepPass`, `StepHailMaryPass`, `StepPuntDirection`, `StepPuntDistance`, `StepRightStuff`, `StepAlwaysHungry`, `StepCatchScatterThrowIn`, `StepSteadyFooting`, `StepThrowKeg`, `StepCatchOfTheDay`, `StepBreatheFire`, `StepMoveBallAndChain`, `StepThrowTeamMate` (re-rolls a Superb throw while the Bullseye dialog is open), `StepHandleDropPlayerContext` |
| State corruption | `StepIntercept` (turn mode not restored), `StepThenIStartedBlastin` (`homePlaying` stays flipped) |

### WP2 — Activation, action declaration and action targets

New services in `util/bb2025`: `PlayerActionLegalityService` (is this action legal for this player now),
`PlayerActionChangeService` (which action changes the current player may make mid-activation),
`BlockTargetService`, `PassDeclarationService`.

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 2.1 ✔ | H | `CLIENT_ACTING_PLAYER` in `step/bb2025/move/StepInitMoving:248-262`, `foul/StepInitFouling:103-118`, `block/StepInitBlocking:168-183`, `ttm/StepInitThrowTeamMate:124-138`, `punt/StepInitPunt:84-99`, `step/mixed/pass/StepInitPassing:163-171` | Only `TurnActionLimitService.isActionAvailable` is checked. Any player id is accepted, including an opposing or already activated player, with any action. `StepInitSelecting:116-120` has the team / active check, these steps do not. | Accept only the current acting player's id (or an empty id to end the action) and only the action changes the client sends for the declared action (take the list from `MoveLogicModule` and the block / pass / foul logic modules). |
| 2.2 | H | `step/bb2025/shared/StepInitSelecting:112-150` | The requested `PlayerAction` is not checked against skills or player state: Hail Mary and bomb actions, Kick 'em, Throw Team-Mate, Multiple Block, Punt work without the skill; a `RESERVE` player with the active bit passes; the action of the current player can be overwritten after it moved. | `PlayerActionLegalityService`: required skill owned and unused, player on the pitch, player state allows the action. |
| 2.3 | H | `step/bb2025/shared/StepInitSelecting:114-150` | Selecting another player while one is acting skips the `EndPlayerAction` sequence (Blood Lust feed, stalling roll, carried-player placement, marking of once-per-game skills). Only the de-select path (empty id, `:152`) runs it. | Ignore a selection while a different acting player is set (confirm the client always de-selects first). |
| 2.4 ✔ | H | `step/bb2025/shared/StepInitSelecting:253-270`, `block/StepInitBlocking:138,153,210-241` | Block target only has to exist. No check of action (a player selected for MOVE can block), adjacency, team, player state, or that a blitz hits the declared blitz target. | `BlockTargetService` in `StepInitBlocking.executeStep` (covers both entry paths): block action with `!hasMoved()` or blitz with defender == selected target; defender in the set `ServerUtilBlock.updateDiceDecorations` computes; own team only with `canBlockSameTeamPlayer`; standing up only with Jump Up. |
| 2.5 ✔ | H | `step/bb2025/shared/StepInitSelecting:280-317`, `step/mixed/pass/StepInitPassing:83-97,122-161,215-251`, `step/bb2025/pass/StepHandOver:78-85` | No `UtilPlayer.hasBall` anywhere in the pass / hand-over sequence; `CLIENT_HAND_OVER` ignores the declared action and `isHandOverUsed()`; Hail Mary and bomb actions need no skill (`canPassToAnySquare`, `enableThrowBombAction` are never checked on the server). | `PassDeclarationService` in the BB2025 `StepInitPassing`: thrower holds the ball (bomb actions excepted), declared action matches the command, skill for Hail Mary / bomb (a catcher re-throwing a bomb stays legal), hand-over only with action `HAND_OVER` and not yet used. |
| 2.6 | M | `step/mixed/pass/StepInitPassing:126-128,142-145,155-157` | Pass target need not be on the pitch (`PassMechanic` is delta-based); hand-over catcher adjacency is tested only after `setBallMoving(true)` (`StepHandOver:78,84`). | Require `FieldCoordinateBounds.FIELD.isInBounds(target)` in the command and `init()` paths; require an adjacent catcher with tackle zones before `EXECUTE_STEP`. Hand-over to an opponent stays allowed (client allows it by design). |
| 2.7 | H | `step/mixed/pass/StepInitPassing:120,124-125,142,163-171` | In `TurnMode.DUMP_OFF` any coach may send the pass; range is not limited to Quick Pass (limit only in `DumpOffLogicModule:61`); `CLIENT_ACTING_PLAYER` is accepted. | Sender must be the coach of `game.getThrower()`; require `PassingDistance.QUICK_PASS`; ignore `CLIENT_ACTING_PLAYER` in `DUMP_OFF`. |
| 2.8 | H | `step/bb2025/shared/StepInitSelecting:242-252`, `foul/StepInitFouling:95-102,146-160` | Foul target only has to exist and not have `preventBeingFouled`. | Require a foul action and a defender from `UtilPlayer.findAdjacentPronePlayers(game, otherTeam, coordinate)`. |
| 2.9 | H | `step/bb2025/shared/StepInitSelecting:271-279`, `move/StepInitMoving:241-247,356-359`, `move/StepHypnoticGaze:106-137` | Gaze victim only has to be on the other team; adjacency and tackle zones are client-only (`GazeLogicModule.canBeGazed`). An own-team or unknown victim falls through to `StepMove` with null coordinates. | Require a gaze action and a victim from `UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, coordinate, false)`; otherwise ignore. |
| 2.10 | H | `step/bb2025/shared/StepInitSelecting:318-339`, `move/StepInitMoving:227-240`, `ttm/StepInitThrowTeamMate:71-77,103-121,166-187` | Thrown player, thrower skill and target are not validated; `TtmMechanic.canThrow` / `findThrowableTeamMates` are never called on the server. Off-pitch targets pass. Re-sending with other ids leaves earlier players in `PICKED_UP`. | Thrown player must be in `findThrowableTeamMates` / `findKickableTeamMates`; thrower must pass `canThrow` / `canKickTeamMates`; target on the pitch; restore the previous player's state when the thrown player changes. |
| 2.11 | H | `step/bb2025/punt/StepInitPunt:84-114,148` | `CLIENT_ACTING_PLAYER`, `CLIENT_FIELD_COORDINATE` and `CLIENT_PUNT_TO_CROWD` are accepted from either coach; the crowd answer needs no dialog; the punter need not hold the ball; the square is not compared with `findPuntSquares`. | Wrap the switch in `checkCommandIsFromCurrentPlayer`; crowd answer only while `DialogPuntToCrowdParameter` is shown; add `UtilPlayer.hasBall`; coordinate must be a punt square. |
| 2.12 | H | `step/bb2025/shared/StepInitSelecting:350-364,554-563` | Multiple block without the skill and without target checks. | Require the skill and exactly two distinct targets from `findAdjacentBlockablePlayers`. |
| 2.13 | H | `step/bb2025/shared/StepInitSelecting:469-473`, `step/mixed/StepThrowKeg:109-114` | Keg target unchecked (rule only in `ThrowKegLogicModule.isValidTarget`). | In the BB2025 `StepThrowKeg`: standing opponent within 3 squares, else ignore. |
| 2.14 | H | `step/bb2025/StepSelectBlitzTarget:95-98,106-177,244-275` | `CLIENT_TARGET_SELECTED`, `CLIENT_USE_SKILL`, `CLIENT_USE_TEAM_MATES_WISDOM`, `CLIENT_CONFIRM` have no sender check. The target only has to be "not on the acting team"; a team-mate id leaves the step without a target selection (probable hang in `StepSelectBlitzTargetEnd:80`). | Gate all four with `checkCommandIsFromCurrentPlayer`; target must be an opposing player on the pitch with `canBeBlocked()`; ignore other ids. |
| 2.15 ✔ | H | `step/bb2025/StepSelectBlitzTarget:161-163,256-259`, `shared/StepInitSelecting:379-381,439-443,454-456` | Any skill named in `CLIENT_USE_SKILL` is added as enhancement to the acting player without ownership (Frenzy, Claws, ignore tackle zones, extra block die, Hail Mary). `skillUsed=false` un-uses Incorporeal after moving. | Require `OfferedSkillService.hasUnused(game, actingPlayer, skill)`; un-use only with `getCurrentMove() == 0`. |
| 2.16 | M | `step/bb2025/block/StepInitBlocking:150-152` | Add-block-die skill accepted without ownership (`StepEndBlocking:98` and `StepInitMoving:299` check it). | `UtilCards.hasUnusedSkill(actingPlayer, skill)`. |
| 2.17 | L | `step/bb2025/move/StepInitMoving:314-320`, `move/StepEndMoving:154-185` | A forwarded `USE_SKILL` with `skillUsed=false` leaves `StepEndMoving` waiting; it then accepts action commands from either coach without null checks. | Forward only when `isSkillUsed()`; add the current-coach check to `StepEndMoving`. |
| 2.18 | L | `step/bb2025/move/StepInitMoving:278-283`, `foul/StepInitFouling:119-124` | `CLIENT_END_TURN` without `EndTurnCommandValidator` (stale command ends the sender's own turn). | Use `EndTurnCommandValidator`. |

### WP3 — Movement

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 3.1 ✔ | H | `step/action/move/StepDivingTackle:118-124`, `step/bb2025/move/StepJump:151-158`; effect in `skillbehaviour/bb2025/DivingTackleBehaviour:72-77`, `step/mixed/move/StepDropDivingTackler:83-89` | Tackler id from either coach, not compared with the eligible tacklers. The named player is moved to the vacated square and dropped. In `StepJump` the answer is also accepted while the jumper's re-roll dialog is open. | Split `StepDivingTackle`. Both steps: `OfferedPlayerChoiceService` with mode `DIVING_TACKLE` (passive coach, id offered or empty). |
| 3.2 | H | `step/mixed/move/StepTentacles:101-111`, `skillbehaviour/bb2025/TentaclesBehaviour:83-126` | Any id from either coach. An unknown id holds the mover without a roll; a strong distant player eases the roll; re-sending during the re-roll dialog rolls again. | Split. `OfferedPlayerChoiceService` with mode `TENTACLES`; in the behaviour hold the mover only if the roll was made. |
| 3.3 | H | `step/bb2025/move/StepShadowing:82-93`, `skillbehaviour/bb2025/ShadowingBehaviour:81-122` | Same shape; on 4+ the named player is teleported to the vacated square. | `OfferedPlayerChoiceService` with mode `SHADOWING`. |
| 3.4 | H | `step/bb2025/move/StepMoveDodge:216-223,276-279,332-334` | Arm Bar choice from either coach at any time; while the dodger's re-roll dialog is open it fails the dodge. | `OfferedPlayerChoiceService` with mode `ARM_BAR`. |
| 3.5 | H | `step/bb2025/move/StepPickUp:175-179,205-218` | Dead `CLIENT_PICK_UP_CHOICE` branch (the dialog is shown and answered in `StepTrickster`); sent during the re-roll dialog it fails the pick-up. | Remove the branch. |
| 3.6 ✔ | H | `step/bb2025/move/StepGoForIt:119-130` | `getSkill()` dereferenced without null check; no sender or offered check, so the opponent can fail the rush during the re-roll dialog. | Null guard; current coach; `OfferedSkillService.offeringPlayerId` equals the acting player. |
| 3.7 | H | `step/bb2025/move/StepHypnoticGaze:89-95,114-118` | `CLIENT_USE_SKILL` from either coach; with 0.6 open an unowned re-roll skill works. | Current coach; `isOffered` + `hasUnused`. |
| 3.8 | M | `step/bb2025/move/StepMoveBallAndChain:148-160,173` | Same for the direction re-roll. | As 3.7. |
| 3.9 | M | `step/bb2025/move/StepJump:163,169`, `StepMoveDodge:191,201`, `StepPickUp:149`, `step/bb2025/action/select/StepJumpUp:101` | Modifier-choice and offered-skill answers are value-checked but accepted from either coach. | `DialogAddresseeService` sender check. |

### WP4 — Block sequence

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 4.1 ✔ | H | `step/bb2025/block/StepPushback:91-98,133-148,215-216,229` | `CLIENT_PUSHBACK` player id and coordinate come from the client; `doPush` only tests that the square is empty. Any player can be moved to any empty square by either coach; a legal square sent early bypasses a pending Stand Firm / Side Step / Grab decision. | Accept only from the coach owning the choice (`PushbackSquare.isHomeChoice`), only while no skill dialog is pending, only a coordinate equal to an unlocked entry of `fieldModel.getPushbackSquares()`; push `state.defender`, not the client's player id. |
| 4.2 | H | `step/bb2025/block/StepPushback:89`, `skillbehaviour/bb2025/StandFirmBehaviour:36`, `SidestepBehaviour:38`, `GrabBehaviour:40` | Skill answers stored from either coach, keyed by the client's player id, also in advance for chain-push victims. | 0.4 plus: the player id must equal the dialog's player. |
| 4.3 | H | `step/bb2025/block/StepBlockRoll:103-108,302-309` | `CLIENT_BLOCK_CHOICE` from either coach; index not bounds-checked (`fBlockRoll[fDiceIndex]`). | Sender must be the coach of the choosing team named by the current `DialogBlockRollPropertiesParameter`; index within the rolled dice. |
| 4.4 | H | `step/bb2025/block/StepBlockRoll:110-177` | Brawler, Hatred, Pro, Consummate, single-die and multi-dice re-roll commands have no sender check. | `checkCommandIsFromCurrentPlayer` on all branches. |
| 4.5 | H | `step/bb2025/block/StepBlockRoll:212-216,326,398` | After a re-roll, a `null`-source re-roll command resets the source and `showBlockRollDialog(true)` offers all re-rolls again; Hatred has no "used" check. | Keep a persisted "re-rolled" flag; once set, offer and accept no further re-roll. |
| 4.6 | L | `step/bb2025/block/StepBlockRoll:136,142,150,162,174,275,286,296` | `dieIndex` / `diceIndexes` used without bounds check; `setReRolledAction` runs before validation. | Validate indexes against the roll; set the action after validation. |
| 4.7 | H | `step/bb2025/mutliblock/StepBlockRollMultiple:157-176,490-494` | Die choice not tied to sender or `isOwnChoice`. | Attacker for `isOwnChoice()` rolls and re-rolls, defender otherwise and only after `attackerTeamSelects` is false; index in range. |
| 4.8 | H | `step/bb2025/mutliblock/StepBlockRollMultiple:250-272` | `state.selectedTarget` / `state.reRollSource` are never cleared, so a re-execution replays the last Brawler / Hatred re-roll. | Clear both after use. |
| 4.9 | M | `step/bb2025/mutliblock/StepBlockRollMultiple:411-438` | `proIndex` and `reRollDiceIndexes` unchecked; single-die sources accept several indexes. | Validate count and range per source. |
| 4.10 | H | `step/bb2025/block/StepBlockDodge:61-70`, `skillbehaviour/bb2025/AbstractDodgingBehaviour:27-28` | Attacker can answer `Dodge = false` for the defender. | 0.4. |
| 4.11 | H | `step/bb2025/block/StepFollowup:66-80,133,142,191-198` | Follow-up choice and Fend answer from either coach at any time; a follow-up choice skips a pending Fend dialog. | Follow-up choice only from the current coach while `DialogFollowupChoiceParameter` is shown; Fend answer only from the defender's coach while offered. |
| 4.12 | H | `step/bb2025/block/StepHitAndRun:94-101,137,153` | Coordinate not compared with `findSquares`; no sender check. | `checkCommandIsFromCurrentPlayer` and `eligibleSquares.contains(coordinate)` (as `StepTrickster:135`). |
| 4.13 | M | `step/bb2025/block/StepTrickster:104-145,189,234-235` | Sender is checked against a `homePlaying` flag the step flips itself; no phase check; `CLIENT_PICK_UP_CHOICE` unchecked. | Skill answer only while `usingTrickster == null`; coordinate / end turn only while `usingTrickster == TRUE` in `TurnMode.TRICKSTER`; pick-up choice only from the defender's coach. Resolve the coach from the defender, not from `homePlaying`. |
| 4.14 | H | `step/bb2025/block/StepEndBlocking:80-81,377` | Pile Driver target not compared with `knockedDownPlayers`. | `usePileDriver = knockedDownPlayers.contains(targetPlayerId)`. |
| 4.15 ✔ | H | `step/action/block/StepDauntless:78-79`, `skillbehaviour/bb2025/DauntlessBehaviour:47-50,62-72`, `skillbehaviour/mixed/IndomitableBehaviour:32-35`, `BlindRageBehaviour:24-26` | The Dauntless hook returns `EXECUTE_STEP` for any `USE_SKILL(Dauntless)`: the failed roll is repeated for free. Indomitable / Blind Rage answers are accepted from the opponent and outside their dialog. | Split the step and the two mixed behaviours. Dauntless hook returns `UNHANDLED_COMMAND`; the others go through 0.4. |
| 4.16 | H | `step/action/block/StepWrestle:80-81`, `skillbehaviour/bb2025/WrestleBehaviour:41-46` | The answer is assigned to whichever flag is still null, whoever sent it. | Split. 0.4; assign the flag by the dialog's player. |
| 4.17 | H | `step/action/block/StepDumpOff:59-60`, `skillbehaviour/bb2025/DumpOffBehaviour:39` | Attacker can decline or force the defender's Dump-Off. | Split. 0.4. |
| 4.18 | H | `step/action/block/StepJuggernaut:79-80`, `skillbehaviour/bb2025/JuggernautBehaviour:36` | Defender can decline the attacker's Juggernaut. | Split. 0.4. |
| 4.19 | M | `step/mixed/multiblock/StepDoubleStrength:71-90` | `isSkillUsed()` is never read ("no" still uses Indomitable); the player id is not compared with `playerIds`; no sender check. | Split. Honour `isSkillUsed()`; `OfferedPlayerChoiceService` with mode `INDOMITABLE`. |
| 4.20 | M | `step/bb2025/shared/StepDropFallingPlayers:110-113`, `skillbehaviour/bb2025/SaboteurBehaviour:37-52,83-102` | Saboteur answer from the wrong coach; a failed attacker roll is repeated on every re-execution because only success is recorded. | 0.4; record that the roll was made. |

### WP5 — Pass, Throw Team-Mate, punt, bomb

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 5.1 | H | `step/bb2025/pass/StepIntercept:117-132` | Interceptor choice and modifier choice are value-checked but accepted from the throwing coach (an empty id cancels the interception). | Sender must be the coach of the non-throwing team. |
| 5.2 ✔ | H | `step/bb2025/pass/StepPass:147`, `StepHailMaryPass:80`, `ttm/StepThrowTeamMate:103`; `skillbehaviour/bb2025/AbstractPassBehaviour:22-24`, `TheBallistaBehaviour:25-28,44-46` | The hooks set the re-roll source for whatever skill the command names; with 0.6 open "The Ballista" re-rolls for any thrower. | 0.4 and 0.6; additionally require `hasUnused(game, thrower, skill)`. |
| 5.3 ✔ | H | `skillbehaviour/bb2025/BullseyeBehaviour:24-34`; effect in `step/bb2025/ttm/StepDispatchScatterPlayer:110-114`, `StepInitScatterPlayer:211-218` | Bullseye is accepted without the skill, without the dialog and for a non-Superb throw; the player lands without scatter. | Require the offered skill, the thrower's unused `canSkipTtmScatterOnSuperbThrow` and `state.passResult == ACCURATE`. |
| 5.4 | H | `skillbehaviour/bb2025/ThrowTeamMateBehaviour:48-51,71-86` | The hook returns `EXECUTE_STEP` for any `USE_SKILL(Throw Team-Mate)`; during the Bullseye dialog this re-rolls the throw. | Return `UNHANDLED_COMMAND`. |
| 5.5 | M | `step/bb2025/pass/StepPass:183-196,243`, `skillbehaviour/bb2025/PassBehaviour:111-124,145` | `state.isPassSkillUsed()` gates only the dialog, not the command: the Pass re-roll can be repeated while a later dialog is open. | Covered by 0.4 (only the offered skill is accepted); keep an explicit `isPassSkillUsed()` check. |
| 5.6 | H | `step/bb2025/pass/StepPass:138-148,202-214,300-301`, `StepHailMaryPass:71-81` | `canAddStrengthToPass` / `dontDropFumbles` answers from either coach; skill ownership and "unused" not checked. | Current coach; `isOffered` + `hasUnused`. |
| 5.7 | H | `step/bb2025/pass/StepMissedPass:84-100`, `special/StepInitBomb:131-140`, `special/StepRecheckExplodeSkill:74-86`, `punt/StepPuntDirection:99-106`, `punt/StepPuntDistance:78-85` | Skill answers from either coach; `getSkill()` not null-checked; the punt steps accept any skill. | Sender check (`DialogAddresseeService`), `isOffered`, null guard. |
| 5.8 | H | `step/bb2025/ttm/StepSwoop:72-90`, `skillbehaviour/bb2025/SwoopBehaviour:52,65-76` | Any skill from any coach is the Swoop answer, at any time (Swoop can be withdrawn after its direction roll); the `CLIENT_SWOOP` coordinate is not compared with the swoop squares; `CLIENT_SWOOP` before the answer unboxes a null `Boolean`. | Skill answer only while `usingSwoop == null` and the Swoop dialog is shown; `CLIENT_SWOOP` only if `usingSwoop` is true and the coordinate is a square from `UtilServerPlayerSwoop.updateSwoopSquares`; thrown player's coach only. |
| 5.9 | M | `step/bb2025/ttm/StepRightStuff:161-174` | Value-checked answers accepted from either coach. | `DialogAddresseeService` sender check. |
| 5.10 | L | `step/bb2025/special/StepInitBomb:135`, `StepRecheckExplodeSkill:79`, `pass/StepMissedPass:95`, `skillbehaviour/bb2025/BullseyeBehaviour:29` | The client's player id is written into `ReportSkillUse`. | Use the acting player's / thrower's id. |

### WP6 — Injuries and ball handling

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 6.1 ✔ | H | `step/bb2025/shared/StepApothecary:153-163,359-366,498-547` | `CLIENT_USE_APOTHECARY` needs only a matching player id: no dialog, no sender check. Re-sent during the keep / take dialog it rolls the apothecary again; sent during a regeneration dialog it re-rolls Regeneration. `apothecaryType` is not compared with `ApothecaryType.forPlayer`. | Accept only in status `WAIT_FOR_APOTHECARY_USE` with `DialogUseApothecaryParameter` shown, from the injured player's coach, with a type from `ApothecaryType.forPlayer`. |
| 6.2 ✔ | H | `step/bb2025/shared/StepApothecary:232-243`, `util/UtilServerInjury.raisePlayer:296-331` | `CLIENT_POSITION_SELECTION` is handled whenever the step waits: team id and position come from the command, a player is raised for that team, and the step ends before the pending injury is applied. | Accept only while `DialogSelectPositionParameter` is shown, from the raising team's coach, with the dialog's team and one of the offered positions. |
| 6.3 | H | `step/bb2025/shared/StepApothecary:219-231`, `StepGettingEven:92-96` | `CLIENT_KEYWORD_SELECTION`: player id and keywords unchecked, any count, any sender. | Use the stored defender id; exactly one keyword from the dialog's list; defender's coach. |
| 6.4 | H | `step/bb2025/shared/StepApothecary:164-218` | Regeneration answers (`USE_SKILL`, `USE_INDUCEMENT`, `USE_RE_ROLL`) have no sender check; `USE_SKILL` has no offered / pending check. | Injured player's coach for all three; for `USE_SKILL` also `isPreRegeneration()`, `isOffered`, `hasUnused`. |
| 6.5 | H | `step/bb2025/mutliblock/StepApothecaryMultiple:135-173,175,191,212,247-253,255-292` | Apothecary and regeneration decisions from either coach; any always-`EXECUTE_STEP` command sent during the apothecary choice dialog applies the original injury and consumes the apothecary. | Sender check against the affected team on every branch; require the matching dialog type. |
| 6.6 | M | `step/bb2025/mutliblock/StepApothecaryMultiple:294-321` | Keyword and position selection unchecked (as 6.2 / 6.3). | As 6.2 / 6.3; derive the team from `deadResults.get(0)`. |
| 6.7 | M | `step/bb2025/mutliblock/StepApothecaryMultiple:144-146,212-227,274` | Any inducement type is consumed for a regeneration re-roll; skill re-roll not tied to dialog or ownership; client-supplied apothecary type. | Require `isPreRegeneration()` and an offered type; `offeringPlayerId` + `hasUnused`; type from `ApothecaryType.forPlayer`. |
| 6.8 | M | `step/bb2025/mutliblock/StepApothecaryMultiple:459,467,493,514` | Re-running `executeStep` while the Getting Even or raise dialog is pending re-applies injuries and re-adds results. | Guard the apply phase with a persisted flag. |
| 6.9 | H | `step/bb2025/shared/StepHandleDropPlayerContext:81-99,132-140` | Skill and player id come from the command: answering with another modification skill skips the Pro roll; `markUsed` is applied to any named player; no sender check. | Ignore unless skill and player id equal the stored ones and the sender is that player's coach. |
| 6.10 | H | `step/bb2025/shared/StepSteadyFooting:146-151` | The other coach can decline Steady Footing. | Falling player's coach only. |
| 6.11 | H | `step/bb2025/shared/StepPlaceBall:70-94,145-148,200-213` | `commandStatus = EXECUTE_STEP` sits outside the matching `if`, so any `USE_SKILL` re-runs `setup()` and can leave `homePlaying` flipped; no sender check; `FIELD_COORDINATE` accepted in any phase. | Return `EXECUTE_STEP` only inside the `if`; ball carrier's coach only; coordinate only in phase `SELECT`. |
| 6.12 ✔ | H | `step/bb2025/shared/StepCatchScatterThrowIn:156-187,242-298,555-558` | Any `CLIENT_PLAYER_CHOICE` (any mode, either coach, also during a catch re-roll dialog) moves the ball to the named player's square. Skill and modifier answers are value-checked but have no sender check. | `OfferedPlayerChoiceService` for the diving-catch choice; `DialogAddresseeService` for the other two. |
| 6.13 | H | `step/mixed/shared/StepPickMeUp:60-70,110-117` | Any ids, duplicates allowed, either coach; on 5+ the named player is set `STANDING`. | Split. `OfferedPlayerChoiceService`. |
| 6.14 | H | `step/mixed/StepQuickBite:86-109`, `step/bb2025/shared/StepInitFeeding:104-123` | Value-checked choices accepted from the other coach (declines Quick Bite; an empty feed choice ends the turn). | Split `StepQuickBite`. Sender must be the coach the dialog addresses. |

### WP7 — Negative traits and special skills

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 7.1 | H | `step/mixed/StepUnchannelledFury:80-93`, `skillbehaviour/bb2025/UnchannelledFuryBehaviour:53-62,81-83` | Any `CLIENT_USE_SKILL` re-runs the step: a failed roll is dropped; Fury of the Blood God grants a second block without ownership. | Split. Handle only an offered, unused skill from the current coach. |
| 7.2 | H | `step/bb2025/shared/StepBloodLust:69-75`, `skillbehaviour/bb2025/BloodLustBehaviour:80-82,110-111` | `CLIENT_BLOODLUST_ACTION` in any state from either coach; during the re-roll dialog it drops the failed roll. | Accept only in `WAIT_FOR_ACTION_CHANGE` from the current coach. |
| 7.3 | H | `step/mixed/shared/StepAnimalSavagery:125-145`, `skillbehaviour/bb2025/AnimalSavageryBehaviour:82-85,128-130,218-232` | Lash-out target not compared with `state.playerIds`; the lash-out-at-opponent skill is accepted without ownership and `markUsed` is applied to the client's player id; no sender or dialog check. | Split. `OfferedPlayerChoiceService` with mode `ANIMAL_SAVAGERY`; skill via `isOffered` + `hasUnused` on the acting player. |
| 7.4 | H | `step/bb2025/StepBalefulHex:82-86,162-184` | Target not compared with `findPlayers()`; any mode, any sender; re-sending during the re-roll dialog rolls again for free. | `OfferedPlayerChoiceService` with mode `BALEFUL_HEX`. Also restrict `findPlayers:231-241` to players on the pitch: it filters by distance only, and dugout coordinates (x = -1…-7 / 30…36) can be within 5 of a player near the end zone. |
| 7.5 | H | `step/bb2025/StepAutoGazeZoat:84-98,172-178`, `StepBlackInk:85-98,172-178` | Target not compared with `findPlayers()`; the opponent can answer or decline. | `OfferedPlayerChoiceService`. |
| 7.6 ✔ | H | `step/bb2025/StepRaidingParty:98-119,197-216` | Neither the player id nor the coordinate is compared with `findPlayers()` / `findSquares()`; no sender check; the coordinate is accepted before a player is chosen; the "no legal squares" path returns without `resetState`. | Current coach; player in `findPlayers`, coordinate in `findSquares(game, player)` and only after the player was chosen; call `resetState` on every exit. |
| 7.7 | H | `step/bb2025/StepThenIStartedBlastin:64-87,100-103,165` | Target unchecked in both phases (rule only in `ThenIStartedBlastinLogicModule.isValidTarget`); no sender check including `CLIENT_END_TURN`; leaving phase 2 does not flip `homePlaying` back. | Phase 1: current coach, standing opponent within 3 of the acting player. Phase 2: the other coach, standing target within 3 of the first target. End turn only from the acting coach in phase 1. Restore `homePlaying` on every exit. |
| 7.8 | H | `step/bb2025/StepWisdomOfTheWhiteDwarf:66-75,117-157`; entries `StepSelectBlitzTarget:166-173`, `shared/StepInitSelecting:464-468` | The granting skill is never required to be unused (once per game, reusable); the target is not compared with `wisePlayers`; no sender check; a non-grantable skill throws. | Start with `UtilCards.hasUnusedSkillWithProperty(player, canGrantSkillsToTeamMates)`; current coach; target and skill from the offered lists; ignore instead of throwing. |
| 7.9 | M | `step/bb2025/StepIllCarryYou:55-66`, `StepPlaceCarriedPlayer:90-99`, `step/mixed/StepFirstMoveFuriousOutburst:90-96` | Values are validated, sender is not (the session is only used for mirroring). | `checkCommandIsFromCurrentPlayer`. Split `StepFirstMoveFuriousOutburst`. |

### WP8 — Fouls and end of turn

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 8.1 | H | `step/bb2025/foul/StepBribes:101-114,144-157,172-186` | After a failed argue, re-sending `CLIENT_ARGUE_THE_CALL` resets the result and rolls again; a non-bribe `USE_INDUCEMENT` re-executes the step; answers accepted from either coach. | Current coach; each answer only while its choice field is `null` and its dialog is shown. |
| 8.2 | H | `step/bb2025/StepEndTurn:185-187,776,797-800,935-936,952-983` | Argue-the-call ids are not compared with `playersForArgue(team)`: duplicates give extra rolls; the opponent's ids suppress his dialog; accepted with the option disabled. | Accept only while the sender's argue choice is `null`; distinct ids from `playersForArgue(team)`; check the game option. |
| 8.3 | M | `step/bb2025/StepEndTurn:160-183` | Bribery and Corruption: `playerIdsNaturalOnes.clear()` runs for any sender; the chosen id is not compared with the list. | Id must be in `playerIdsNaturalOnes` and belong to the sender's team before clearing. |
| 8.4 | M | `step/bb2025/StepEndTurn:195,844-859,958-962` | "Only one bribe per send-off" is used only to build the dialog; duplicate ids roll again. | Filter to distinct ids from `playersForBribe(team)` before `useInducement`. |
| 8.5 | H | `step/bb2025/StepEndTurn:199-209,517-521` | Star of the Show answer is not bound to the dialog, the scorer or the sender. | Accept only while `useStarOfTheShow == null`, with `offeringPlayerId(game, skill)` equal to the command's player and `checkCommandIsFromPlayerTeam`. |
| 8.6 | M | `step/bb2025/StepEndTurn:188,194,517` | An argue or bribe command during the Star of the Show dialog sets `fWithinSecretWeaponHandling`, skips scoring and unboxes a null `Boolean`. | Accept each command only while its own choice field is `null` and no other answer is pending. |
| 8.7 | — | `util/bb2025/OfferedReRollSourceService:38-80` | The service has no branch for `DialogBriberyAndCorruptionParameter`, so the client's `CLIENT_USE_RE_ROLL(ARGUE_THE_CALL)` answer may always be dropped. Functional question found during the audit. | Confirm with a test; add the branch if confirmed. |

### WP9 — Setup and kickoff events

New `util/bb2025` services: `SetupCommandService` (replaces the `UtilServerSetup` calls of BB2025 steps),
`SetupCoordinateService` (replaces `AbstractStep.setPlayerCoordinates` for BB2025 steps).

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 9.1 ✔ | H | `util/UtilServerSetup.setupPlayer:122-163`; callers `step/bb2025/kickoff/StepSetup:66-71`, `StepApplyKickoffResult:172-183,612`, `StepSwarming:87-90`, `StepDwarfenWisdom:123-127` | Checks only "player on the playing team" and "square empty". Any player state becomes `STANDING` (KO, casualty, banned); any coordinate is accepted (opposing half, off pitch); no sender check. | `SetupCommandService`: sender is the current coach, `playerState.canBeMovedDuringSetup()`, coordinate in the own half or own reserve box. |
| 9.2 ✔ | H | `step/AbstractStep.setPlayerCoordinates:327-351`; callers `StepSetup:74`, `StepApplyKickoffResult:190`, `StepSwarming:81`, `StepDwarfenWisdom:118` | The `CLIENT_END_TURN` coordinate map is applied to any player with `canBeMovedDuringSetup()`: no team, null, coordinate or occupancy check, applied before validation and not rolled back. Opposing players can be sent to the box. Unknown ids throw. | `SetupCoordinateService`: skip entries whose player is unknown or not on the acting team, whose coordinate is null or outside the own half / own box, or whose square is taken. |
| 9.3 ✔ | H | `mechanic/bb2025/SetupMechanic.checkSetup:45-76` | Counts only own players inside the own half; players elsewhere on the pitch are neither counted nor rejected. | Fail if any own player on the pitch is outside the own half. |
| 9.4 | M | `step/bb2025/kickoff/StepSetup:50-65`, `util/UtilServerSetup:35,62,71-73,92` | Load / save / delete of saved setups act for the playing team regardless of sender (the other coach can overwrite or delete them in the database); saved coordinates are applied without checks; fewer coordinates than player numbers throws. | `checkCommandIsFromCurrentPlayer` on all three; validate loaded coordinates with `SetupCoordinateService`; ignore mismatched arrays. |
| 9.5 | H | `step/bb2025/kickoff/StepApplyKickoffResult:172-193,401-408` (High Kick) | Any number of players to any squares, from either coach; no validation on end turn (rule only in `HighKickLogicModule`). | Current coach; one active own player on the pitch; target = ball square. |
| 9.6 | H | `step/bb2025/kickoff/StepApplyKickoffResult:173-177,588-647` (Quick Snap) | No sender, team, active or adjacency check; `nrOfMovedPlayers++` runs before the move is rejected, so the opponent can exhaust the allowance. | Current coach; active own player on the pitch; adjacent target; count only accepted moves. |
| 9.7 | H | `step/bb2025/kickoff/StepApplyKickoffResult:195-209,704,716-729` (Charge) | Only eligibility is checked; `nrOfPlayersAllowed` is only sent to the dialog. | `OfferedPlayerChoiceService` (enforces max selects). |
| 9.8 | H | `step/bb2025/kickoff/StepApplyKickoffResult:178-181,289-301,337-340,369-380` (Solid Defence) | Only players that were open at event start are tracked; marked players and reserves can be moved. | Snapshot all own players at event start; every changed coordinate must belong to a selected player. |
| 9.9 | M | `step/bb2025/kickoff/StepApplyKickoffResult:195-211` | Any `CLIENT_PLAYER_CHOICE` from the current coach executes the step; mode `SOLID_DEFENCE` with an empty list sets that turn mode whatever the kickoff result is. | Accept only when `fKickoffResult` and mode match and no selection was made yet. |
| 9.10 | H | `step/bb2025/kickoff/StepSwarming:87-90,106-123,156-159` | Any player can be placed or moved; no sender check. | Current coach; only players that were `RESERVE` with the `LINEMAN` keyword at step start; own half. |
| 9.11 | H | `step/bb2025/kickoff/StepDwarfenWisdom:107-145,178,221-258` | Setup commands are accepted before or without using the inducement; a second `USE_INDUCEMENT` re-rolls the D3; the type is not compared with the offered one and the result of `useInducement` is ignored. | Setup only in `TurnMode.DWARFEN_WISDOM` for selected players; ignore `USE_INDUCEMENT` once confirmed; type must equal the offered type and `useInducement` must succeed. |
| 9.12 ✔ | H | `step/bb2025/kickoff/StepTouchback:64-72,112-118` | Either coach can place the ball anywhere (the session is only used for mirroring; rule only in `TouchbackLogicModule.isPlayerSelectable`). | Receiving coach only; non-null coordinate; a receiving-team player with tackle zones and without `preventHoldBall`, or any pitch square if there is none. |
| 9.13 | H | `step/phase/kickoff/StepReceiveChoice:67-71,86-92` | The coin-toss loser can make the choice. | Split. Sender must be the coach of `fChoosingTeamId`. |
| 9.14 | L | `step/phase/kickoff/StepCoinChoice:56-60` | No sender check (no gain, wrong name in the report). | Split. `checkCommandIsFromCurrentPlayer`. |
| 9.15 | H | `step/bb2025/kickoff/StepKickoffScatterRoll:80-93`, `StepKickoffScatterRollAskAfter:91-97` | The receiving coach can answer the Kick dialog. | `checkCommandIsFromCurrentPlayer`; null guard on the skill. |
| 9.16 | H | `step/bb2025/kickoff/StepKickoffResultRoll:58-62` | Any coach chooses any `KickoffResult` (only reachable with the overtime "choose" option). | Current coach; only `CHARGE` or `SOLID_DEFENCE`. |

### WP10 — Pre-game, inducements, end of game

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 10.1 ✔ | H | `step/bb2025/start/StepBuyInducements:111-148,342-365,579` | The team is taken from the command (anything that is not the home id is treated as away); the sender is never compared with it; no phase or "already submitted" check; used gold is overwritten, not summed. A coach can skip the opponent's purchase, submit twice and pay once, or spend the opponent's treasury. | Sender must be the coach of `command.getTeamId()`; the team must match the phase; one submission per team. |
| 10.2 ✔ | H | `step/bb2025/start/StepBuyInducements:150-185,397,415-417,462,525,568-574` | The only value check is total cost ≤ available gold. Counts may be negative (negative cost pays for everything else); no maximum / availability check; types without cost id are free; `uses` is copied from the client; positions are not checked for type, duplicates or limits; mercenary skills are arbitrary; prayers are unbounded. | `InducementPurchaseService`: per inducement `0 < value <= type.availability(team, options)`, `uses == 0`, a real cost id; positions of the right type and within limits; skills from the position's categories; over-budget is ignored, not thrown. Limits come from what the server already has (decided): game options, `InducementType.availability` and the team's roster positions; no external rule source. |
| 10.3 | H | `step/bb2025/inducements/StepInitInducement:103-108,130-143` | Any coach, any inducement type; the type is not compared with `findUseableInducements()`. | Sender must be the coach of `fHomeTeam`; type is null or one of the offered types. |
| 10.4 | H | `step/bb2025/inducements/StepWeatherMage:53-65,78-84` | Any weather value from any sender (the offered options are a local variable); `CLIENT_CONFIRM` from the opponent. | Store the offered options; accept only from the mage's coach and only an offered name; confirm only for the information dialog. |
| 10.5 | H | `step/bb2025/StepWizard:105-117,150-153`, `special/StepSpecialEffect:122-146` | The opponent can cast or cancel the wizard; a null coordinate from the away coach throws. The target coordinate is not required to be on the pitch: Zap takes `fieldModel.getPlayer(fTargetCoordinate)`, so a dugout coordinate zaps a benched player (`StepSpecialEffect` has no on-pitch check). | Sender must be the coach matching `fHomeTeam`; null guard; target inside `FieldCoordinateBounds.FIELD`. |
| 10.6 | M | `step/bb2025/StepWizard:144-148`, `kickoff/StepDwarfenWisdom:178`, `inducements/StepThrowARock:100` | The boolean result of `UtilServerInducementUse.useInducement` is ignored, so a used-up inducement may be usable again. | Require `hasUsesLeft(type)` and apply the effect only if `useInducement` returns true. |
| 10.7 ✔ | H | `step/bb2025/StepPrayer:71-81,118`; `inducements/mixed/prayers/IntensiveTrainingHandler:47-51`, `SelectPlayerPrayerHandler:26-30` | Player id and skill for Intensive Training / Iron Man / Knuckle Dusters are passed to the handlers unchecked, from any sender. | In `StepPrayer`: sender is the praying team's coach; id and skill come from the `DialogSelectSkillParameter` / `DialogPlayerChoiceParameter` currently shown. The mixed handlers stay untouched. |
| 10.8 | H | `step/bb2025/end/StepMvp:74-88,121-131,185-202` | Nominations are not compared with `findPlayerIdsForMvp(team)` or `maxSelects`; unlimited commands add awards. | Exactly `maxSelects` distinct ids from the own team's list; ignore once `fNrOf*Choices >= fNrOf*Mvps`. |
| 10.9 | M | `step/bb2025/end/StepAssignTouchdowns:69-77,113-124` | Any coach, any player id. | `OfferedPlayerChoiceService` with mode `ASSIGN_TOUCHDOWN`. |
| 10.10 ✔ | M | `step/bb2025/end/StepMvp:134-170` | Not an input gap, found during the audit: with MVP nominations enabled and exactly one eligible player (or none), the step adds the player (or sets the MVP count to 0) and returns with neither a dialog nor a next action, so the game waits forever. | Loop until a dialog is shown or all MVPs are assigned, then continue to the existing completion path. |

### WP11 — Remaining crash-only inputs

Most of these disappear with the gates above; what remains gets a null / membership guard that ignores
the command. Check each location after WP0–WP10 and add a guard only where the input is still reachable.

| Kind | Locations |
|---|---|
| `getSkill()` null in `CLIENT_USE_SKILL` | `StepSelectBlitzTarget:110`, `StepInitMoving:299,323`, `StepInitBlocking:150`, `StepFollowup:68`, `StepTrickster:120`, `StepEndBlocking:86`, `StepApothecaryMultiple:214`, `StepApothecary:210`, `StepPass:140`, `StepHailMaryPass:73`, `StepUnchannelledFury:82`, `StepAnimalSavagery:135`, `StepSteadyFooting:148`, `StepPlaceBall:72-73`, `StepDoubleStrength:73`, `StepHandleDropPlayerContext:89`, `StepQuickBite:102`, `StepInitSelecting:379` |
| Unknown player id | `StepPushback:215`, `StepInitThrowTeamMate:111-114,176-177`, `StepInitSelecting:117,556,562`, `StepApplyKickoffResult:595`, `StepMvp:189-191`, `StepAssignTouchdowns:114-115`, `StepPickMeUp:110-116`, `StepThrowKeg:114`, `StepEndTurn:203`, `DivingTackleBehaviour:76`, `ShadowingBehaviour:121`, `AnimalSavageryBehaviour:225` |
| Null coordinate (`.transform()` for the away coach) | `StepInitPassing:128`, `StepInitPunt:106`, `StepSwoop:80`, `StepFirstMoveFuriousOutburst:82`, `StepSecondMoveFuriousOutburst:99` |
| Missing game object | `StepInitBlocking:152` (no target selection state), `StepWizard:152,172` (Zap on a square without a player, after the wizard was consumed) |
| Null / empty lists and indexes | `StepApothecary:226,237-238,502`, `StepApothecaryMultiple:314-316`, `StepCatchScatterThrowIn:158`, `StepInitSelecting:358`, `StepEndTurn:175,193`, `StepBribes:109` |
| Null action | `StepInitMoving:261`, `StepInitFouling:112`, `StepInitPassing:166`, `StepEndMoving:169` |
| Explicit exceptions on client values | `StepApplyKickoffResult:202-204`, `StepDwarfenWisdom:135-140`, `StepWeatherMage:56`, `StepWisdomOfTheWhiteDwarf:149-150`, `StepBuyInducements:142-146,178-182,397,415,462,525` |
| Illegal punt input | `StepPuntDirection:141-144` → `ThrowInMechanic:73`; crowd punt away from the edge → `mechanics/bb2025/ThrowInMechanic:52` |

### WP12 — Command handlers and socket layer (all rulesets)

These commands are handled by `handler/ServerCommandHandler*` before any step sees them. They are
reachable by spectators and partly by sockets that have not joined anything. The code is not ruleset
specific, so the fixes apply to all rulesets.

| # | Sev | Location | Gap | Fix |
|---|---|---|---|---|
| 12.1 ✔ | H | `net/ServerCommunication:139-143,200-228`; trigger e.g. `handler/ServerCommandHandlerJoin:43` | An exception in any handler calls `shutdown(command, any)`, which disconnects every session of the sender's game (or shared replay). Handlers run for spectators too. `CLIENT_JOIN` without a client mode dereferences `getClientMode().getName()`; sent by a spectator this shuts down the match being watched, repeatably. | On a handler exception for a client command, log it and close only the sending session. Null-guard `ServerCommandHandlerJoin:43,63` and `ServerCommandHandlerJoinReplay:50`. |
| 12.2 ✔ | H | `handler/ServerCommandHandlerJoinReplay:47-60`, `net/ReplaySessionManager.addSession`, `transferControl` | No authentication: any socket joins any shared replay under a client-supplied coach name. Names are not unique, so a session can pose as another participant in chat and receive control when it is transferred to that name. Only the displayed replay name is truncated; the map key is the full client string. | Require a session that was authenticated through `CLIENT_JOIN` with mode `REPLAY` and take the coach name from that; reject a second session with the same name in one replay; build the key from the truncated name. |
| 12.3 ✔ | M | `handler/ServerCommandHandlerReplay:33-57` | No authentication and a client-supplied game id: every command runs a blocking DB read on the single command thread and puts the loaded game into the game cache. | Require a joined (or replay-authenticated) session; move the DB load to the request processor; do not add games loaded only for a replay to the live game cache. |
| 12.4 ✔ | M | `handler/ServerCommandHandlerLoadAutomaticPlayerMarkings:21-25`, `request/fumbbl/FumbblRequestLoadPlayerMarkingsForGameVersion` | No authentication; the command carries a complete `Game` and a coach name. The server loads that coach's auto-marking configuration from FUMBBL and the settings table and returns markings computed with it. | Require an authenticated session and use its coach, not the command's. |
| 12.5 | M | `handler/AbstractServerCommandHandlerSketch:26-36`, `ServerSketchManager:17-37`, `ReplayState:53-59` | "Prevent sketching" is stored and broadcast but never checked when a sketch command arrives. Number of sketches and coordinates per session is unlimited and every one is broadcast. A sketch without id makes later `sketch.getId().equals(id)` throw, which with 12.1 open shuts the replay down. | Ignore sketch commands of coaches in `coachesPreventedFromSketching`; cap sketches per session and coordinates per sketch; ignore sketches without id. |
| 12.6 ✔ | L | `ReplayState:19-31`, `handler/ServerCommandHandlerReplayStatus:32-45` | `ReplayState` stores `CLIENT_REPLAY_STATUS` from any replay session; only the push to others checks `hasControl`. A participant without control changes the state new joiners receive and can suppress the controller's next update (it is only pushed if it differs from the stored state). | Store the status only if the session has control. |
| 12.7 ✔ | M | `handler/ServerCommandHandlerSetMarker:53-85` | Text is truncated and only coaches may set markers, but the coordinate is not required to be on the pitch and the player id is not required to exist. Each new id or coordinate adds a marker to the field model, which is synced to all clients. | Coordinate inside `FieldCoordinateBounds.FIELD`; player id must be a player of the game. |
| 12.8 | L | `handler/ServerCommandHandlerTalk:42-110`, `ServerCommandHandlerUserSettings:35-49` | Talk and setting values have no length limit of their own (only the 64 KB message limit) and talk has no rate limit; every talk line is broadcast and, in FUMBBL mode, uploaded. | Cap talk length and setting value length; rate-limit talk per session. |

Checked and sound, no work needed:

- **Internal commands cannot be injected.** `NetCommandId.createNetCommand` has no case for internal
  ids and throws; `CommandSocket.onTextMessage` catches it. Malformed JSON ends there as well.
- **Chat slash-commands** are gated on the server (`handler/talk/TalkHandler.handles`,
  `TalkRequirements`): test commands need a coach session in a test game or on a test server, live
  state-edit commands need a non-coach session with the `STATE_EDIT` account property, and each handler
  runs inside its own try/catch.
- **Marker text** is truncated to `IDbTablePlayerMarkers.MAX_TEXT_LENGTH`; each coach can only write
  their own side of a marker.
- **User settings** are stored for the session's coach, not for a name from the command.
- **Replay control** (`CLIENT_TRANSFER_REPLAY_CONTROL`, `CLIENT_SET_PREVENT_SKETCHING`) requires the
  sending session to have control.
- **Steps** only receive commands from the two coach sessions (`ServerCommunication:185-190`).

Not checked: what FUMBBL verifies in the authorisation, team-load and game-check requests
(`request/fumbbl/*`), and when games loaded through 12.3 leave the cache.

---

## 7. Tests

- **Every fix starts with a failing test** that sends the illegal command and asserts that game state is
  unchanged and the step did not execute. This doubles as confirmation of the unverified rows (§2).
- Decision logic lives in services (§4.3) and is unit tested there with JUnit 5 and Mockito, as
  `OfferedSkillServiceTest` and `OfferedReRollSourceServiceTest` do.
- Per service: wrong sender, no / wrong dialog, value outside the offered options, and one accepted case
  that mirrors what the client sends.
- Base-class gates (0.1, 0.2, 0.4, 0.7): one test per ruleset mechanic showing that `bb2016` / `bb2020`
  still accept and `bb2025` rejects.
- No step-level test harness exists today. Add step tests only where the fix is in step state rather
  than in a service (4.5, 4.8, 6.8, 6.11, 7.6, 7.7).
- Command: `mvn --batch-mode clean verify` (CI), or `mvn test -Dtest=<TestClass>` for a single class.

## 8. Change list

These are fixes to released behaviour, so each work package adds bugfix entries to the top
`VersionChangeList` in `ffb-client-logic/.../client/model/ChangeList.java`, one per topic rather than
one per row, in the style of the existing "Tasty Morsel: The server did not verify that …" entry.
No rules version in the text.

## 9. Decisions

1. **Shared concrete steps.** Split (§3.1). Shared concrete steps and behaviours are duplicated
   into the ruleset packages; they do not call `CommandValidationMechanic`.
2. **Feedback for rejected commands.** None for the sender; rejections are logged at warn level (§3.2).
3. **Inducement purchase limits (10.2).** Taken from the game options and roster data the server already
   has (`InducementType.availability`, roster positions).
4. **`StepMvp` with a single candidate.** Confirmed by reading the code; added as 10.10.
5. **Places the step audit had not covered.** Read afterwards:
   - `step/mixed/StepInitLookIntoMyEyes`, `step/bb2025/StepTreacherous`,
     `step/bb2025/inducements/StepThrowARock`: no client input; targets are computed by the server.
     Only 10.6 (ignored `useInducement` result) applies to `StepThrowARock`.
   - Zap on a dugout coordinate: real, added to 10.5.
   - `StepBalefulHex.findPlayers`: real, added to 7.4.
6. **Corrupted state** (4.13, 6.11, 7.7, 9.9). The fixes prevent the corruption. No repair of running or
   stored games and no further tracing of the downstream effects.
7. **Other rulesets.** Steps stay BB2025 only; `bb2016` and `bb2020` are not audited or changed.
8. **Command handlers.** Audited; findings are WP12 and apply to all rulesets.

## 10. Still open

- What FUMBBL checks in the authorisation, team-load and game-check requests was not looked at (WP12).
- Whether an off-pitch pass target ends in an exception or a lost ball (2.6); the fix rejects it either way.
- 8.7: whether the Bribery and Corruption re-roll answer is dropped today needs a test.

# BB2025: Timeout Enforcement and Timing Configurations

Status: **planned / not implemented**.
Scope: timeout enforcement (Part A) is **BB2025 only**; `bb2016` and `bb2020` keep their current
behaviour. Timing configurations (Part B) live in ruleset independent server code, but their
enforcement relies on the BB2025 hooks from Part A (see §9.1).

---

## 1. Goal

A timeout forced by the waiting coach (or, with Part B, by an exhausted time bank) must end the
acting coach's turn **without** requiring the acting coach to send another command. Today the turn
only ends once the acting coach is back in player selection and clicks something.

Additionally, offer timing configurations beyond the fixed per-turn limit: per-coach time pools and
per-turn allowances backed by a time bank, with automatic timeouts when the bank/pool is exhausted.

---

## 2. Current behaviour (analysis)

### 2.1 Turn time accounting

| Piece | Location | Behaviour |
|---|---|---|
| Turn clock start | `GameState.turnTimeStarted` (transient) | Set via `UtilServerTimer.startTurnTimer`, cleared via `stopTurnTimer`. |
| Start/stop calls | `StepEndTurn` (bb2025 l.411/531), `StepBlitzTurn`, `StepPlaceBall`, `SidestepBehaviour`, `UtilServerDialog.showDialog/hideDialog`, `ServerCommandHandlerSocketClosed` | Clock pauses while the passive coach answers a dialog (`showDialog(..., stopTurnTimer=true)`) or a coach disconnects. |
| Elapsed turn time | `Game.turnTime` (no observer notification) | Updated by `UtilServerTimer.syncTime`. |
| Tick | `ServerGameTimeTask` (1 s, `FantasyFootballServer` l.244) | Calls `syncTime`, sends `ServerCommandGameTime`, and if the limit is passed sets `Game.timeoutPossible` and syncs the model. Runs on a `java.util.Timer` thread, **not** the command thread. |
| Options | `GameOptionId.TURNTIME` (int, default 240 s), `GameOptionId.TIMEOUT_ALLOWED` (bool, default true) | Single per-turn limit, manual timeout only. |

### 2.2 Timeout enforcement

1. `Game.timeoutPossible` becomes `true` → client of the waiting coach shows the *Timeout* button
   (`TurnDiceStatusComponent.drawTimeoutButton`, `ClientStateWaitForOpponent` → `ClientCommandIllegalProcedure`).
2. The command reaches **whatever step is current**. `AbstractStep.handleIllegalProcedure` adds
   `ReportTimeoutEnforced`, sets `Game.timeoutEnforced = true`, `timeoutPossible = false`, syncs the
   model and returns `UNHANDLED_COMMAND`. No step action is set.
3. The only reader of `timeoutEnforced` is `StepInitSelecting.executeStep()` (all three rulesets),
   which publishes `END_TURN` (+ `CHECK_FORGO` in bb2025) and jumps to its end label.
4. `StepInitSelecting.executeStep()` is only reached through `handleCommand` from the **acting**
   coach. `start()` does not check the flag.
5. `Game.startTurn()` resets both flags.

### 2.3 Resulting gaps

| Situation of the acting coach | Effect of an enforced timeout |
|---|---|
| Idle in player selection | Nothing until the acting coach sends any selection command. |
| Mid activation, waiting for input (`StepInitMoving`, `StepInitBlocking`, …) | Nothing; the activation continues indefinitely. Only after the activation ends and another selection command arrives is the turn ended. |
| Answering own dialog (re-roll, skill use, …) | Nothing; same as above. |
| Disconnected / AFK | The turn clock is stopped on disconnect; otherwise the game stalls until the coach returns. |

Further issues:

* `handleIllegalProcedure` does not verify that the sender is the **passive** coach (only coach
  sessions reach the step, see `ServerCommunication.handleByGameState`, but the acting coach could
  time out themselves).
* `ServerGameTimeTask` mutates `Game` from the timer thread. Not changed by this plan, but no new
  logic may add further game mutation on that thread (see §8.3).

---

# Part A — Making an enforced timeout take effect

The three measures below are complementary. Together they guarantee that an enforced timeout ends
the turn at the earliest *safe* point:

* §3 immediately, if the acting coach is in player selection,
* §4 immediately, if the acting coach is mid activation and waiting for an action command,
* §5 at the end of the current activation in every other case (dice rolls, dialogs, chained
  sequences), i.e. the moment the acting player is set to inactive.

Rules decision: an enforced timeout is treated as a **voluntary end of turn**, consistent with the
current bb2025 `StepInitSelecting` (`CHECK_FORGO = true`, so stalling checks apply; see
`StepForgoneStalling`). This is kept unless decided otherwise (§10).

## 3. Selection idle — `step/bb2025/shared/StepInitSelecting`

* In `start()`: if `game.isTimeoutEnforced()`, call `executeStep()` (which already handles the flag).
  This covers returning to selection after an activation ended while the flag was pending.
* Handle the timeout command itself (see §4.1 hook): if the current step is `StepInitSelecting`,
  call `executeStep()` immediately.

## 4. Mid activation, waiting for a command

### 4.1 Hook in `AbstractStep`

* Add `protected StepCommandStatus onTimeoutEnforced()` to `AbstractStep`, default returns
  `UNHANDLED_COMMAND` (no-op ⇒ bb2016/bb2020 unchanged).
* `handleIllegalProcedure` calls the hook after setting `timeoutEnforced` and returns its status.
  `StepExecutor` then processes the step result as for any handled command.
* Memory note: returning `UNHANDLED_COMMAND` while having set a next action is not a wait — the hook
  must only set a next action when it really acts.

### 4.2 bb2025 overrides

Steps that wait for the acting coach and already accept `CLIENT_END_TURN` override the hook and
reuse their own end-turn path (`fEndTurn = true`, publish `END_TURN` + `CHECK_FORGO`, jump to the
end label). This keeps the action's own clean-up (end steps, `EndPlayerAction` sequence) intact.

| Step | Package | Note |
|---|---|---|
| `StepInitSelecting` | `bb2025/shared` | §3 |
| `StepInitMoving` | `bb2025/move` | `executeStep()` with `fEndTurn` |
| `StepInitBlocking` | `bb2025/block` | |
| `StepInitFouling` | `bb2025/foul` | |
| `StepInitThrowTeamMate` | `bb2025/ttm` | |
| `StepInitPunt` | `bb2025/punt` | |
| `StepSelectBlitzTarget` | `bb2025` | |
| `StepAutoGazeZoat`, `StepBlackInk`, `StepRaidingParty`, `StepIllCarryYou`, `StepThenIStartedBlastin`, `StepHitAndRun`, `StepTrickster` | `bb2025/...` | only where they wait for an acting coach command; otherwise rely on §5 |
| `StepInitPassing`, `StepInitFuriousOutburst`, `StepSecondMoveFuriousOutburst`, `StepPlayCard` | `mixed` | **Not overridden.** Covered by §5. Only if immediate handling is required, duplicate them into `bb2025` (no inheritance between rulesets) and override there. |

Kickoff/setup steps (`StepSetup`, `StepSwarming`, `StepDwarfenWisdom`, `StepApplyKickoffResult`)
are out of scope (timeout is not offered outside `REGULAR` turns in practice — verify, §10).

Bomb steps (`bb2025/special/StepInitBomb`) wait for the bomb throw target; include if they accept
an end-turn equivalent, otherwise rely on §5.

## 5. Every other case — cut-off when the acting player is set to inactive

Instead of checking the flag in each `StepEnd*` step, the turn is cut off at the single point where
every bb2025 activation ends: **`step/bb2025/shared/StepEndFeeding.executeStep()`**. All action
sequences end in `EndPlayerAction` (`generator/bb2025/EndPlayerAction`) → `END_FEEDING`, where
`UtilServerSteps.changePlayerAction(this, null, null, false)` clears the acting player, which in
`UtilActingPlayer.changeActingPlayer` sets the previous player inactive (`changeActive(false)`) and
removes activation-scoped enhancements. Right after this, `StepEndFeeding` decides between pushing
the `Select` sequence (next activation) and the `Inducement`/`EndTurn` sequences.

Change:

```java
// StepEndFeeding.executeStep(), bb2025 only
fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
if (!fEndTurn && game.isTimeoutEnforced() && game.getTurnMode() == TurnMode.REGULAR) {
    fEndTurn = true;
    checkForgo = true;
}
```

Notes:

* Running at `StepEndFeeding` means the ball has been resolved, feeding/stalling checks of the
  activation are done and the player has been deactivated — no partially resolved rolls are cut.
* `TurnMode.isPassBlock()` / `KICKOFF_RETURN` branches are left untouched (those turns are not the
  acting coach's regular turn, the regular turn continues afterwards and §3 picks up the flag).
* If the touchdown check already ended the turn, the timeout has no extra effect.
* This also covers the `mixed` steps of §4.2 and any dialog the acting coach is answering, because
  the activation still ends through `StepEndFeeding`.
* Optional extension (not planned for the first iteration): after a grace period, auto-answer a
  pending dialog of the acting coach with its default/decline so a coach idling in a dialog cannot
  delay §5 indefinitely. With Part B this becomes relevant (see §8.4).

## 6. Hardening

* `handleIllegalProcedure`: accept only if
  `UtilServerSteps.checkCommandIsFromPassivePlayer(gameState, command)` (the server-side automatic
  timeout of Part B bypasses this via its own internal command, §8.3).
* Ignore the command if `timeoutEnforced` is already set (avoid duplicate reports).
* `ReportTimeoutEnforced` stays as is for manual timeouts; Part B adds an `automatic` flag (§8.5).

## 7. Tests (Part A)

`ffb-statetest` (`GameStateBuilder` / `StepEngine` / `Commands` / `TestRolls`), run with
`mvn test -pl ffb-statetest -Dtest=TimeoutEnforcementTest`:

1. Timeout possible, acting coach idle in selection → illegal procedure from passive coach ends the
   turn immediately (turn passes to the opponent, stalling check applied).
2. Acting player mid move (waiting in `StepInitMoving`) → turn ends immediately, player deactivated,
   ball handled as on a voluntary end turn.
3. Acting player in a block dialog → the block resolves; after the activation the turn ends without
   another selection command.
4. Pass action (`mixed/StepInitPassing`) → turn ends at `StepEndFeeding`.
5. Illegal procedure from the acting coach is ignored.
6. Touchdown during the activation together with an enforced timeout → single end-turn.
7. bb2020 regression: behaviour unchanged (flag only evaluated by selection).

---

# Part B — Timing configurations

## 8. Design

### 8.1 Timing modes

| Mode | Per-turn allowance | Bank / pool | Manual timeout | Automatic timeout |
|---|---|---|---|---|
| `TURN` (current, default) | `TURNTIME` | — | after allowance (`TIMEOUT_ALLOWED`) | optional, after allowance + grace |
| `TURN_WITH_BANK` | `TURNTIME` | per coach bank, drained only by overrun of the allowance | optional, once bank is exhausted | when bank is exhausted |
| `POOL` (chess clock) | — (optional increment per turn) | per coach pool, drained by all own turn time | — | when pool is exhausted |

Semantics:

* **Bank**: `remaining = bank - max(0, turnTime - allowance)`. Unused allowance is **not** added to
  the bank unless `TIME_BANK_CARRY_OVER` is enabled.
* **Pool**: `remaining = pool - turnTime`; at turn end `pool += TIME_POOL_INCREMENT` (Fischer).
* **Scope**: bank/pool reset per `GAME` or per `HALF` (`TIME_BANK_SCOPE`); overtime gets
  `TIME_BANK_OVERTIME` seconds.
* **Exhausted pool/bank**: `TIME_EXHAUSTED_POLICY`:
  * `AUTO_TIMEOUT` — every following turn is timed out as soon as the allowance (or, for `POOL`,
    `TIME_EXHAUSTED_TURN_TIME`) passes,
  * `MIN_TURN_TIME` — coach falls back to a fixed per-turn time `TIME_EXHAUSTED_TURN_TIME`, then
    automatic timeout.
  (Instant timeout of every turn would make the game unplayable, so a minimum per-turn time is
  always granted.)
* Only the acting coach's clock runs. Time spent on the passive coach's dialogs stays unaccounted
  (current behaviour, clock stopped by `UtilServerDialog`) — see §10 for an alternative.
* Setup, kickoff and blitz turns: timed with the current per-turn clock only, not charged to
  bank/pool in the first iteration (§10).

### 8.2 Options

New `GameOptionId`s (created in `GameOptionFactory`, descriptions shown in the game options dialog):

| Id | Type | Default |
|---|---|---|
| `TIMING_MODE` | `GameOptionString` (`turn`, `turnWithBank`, `pool`) | `turn` |
| `TIME_BANK` | int, seconds | 0 |
| `TIME_BANK_SCOPE` | string (`game`, `half`) | `game` |
| `TIME_BANK_CARRY_OVER` | bool | false |
| `TIME_BANK_OVERTIME` | int, seconds | 0 |
| `TIME_POOL_INCREMENT` | int, seconds | 0 |
| `TIME_EXHAUSTED_POLICY` | string (`autoTimeout`, `minTurnTime`) | `autoTimeout` |
| `TIME_EXHAUSTED_TURN_TIME` | int, seconds | 30 |
| `AUTO_TIMEOUT_GRACE` | int, seconds | 0 |

`TIME_BANK` doubles as the pool size in `POOL` mode. Existing games without the new options behave
exactly as today (`TURN`, no automatic timeout).

### 8.3 Server architecture

* Replace the static `UtilServerTimer` usages step by step by an instantiable `TurnTimerService`
  (repository convention: instance services). The service owns start/stop/sync and delegates
  limit evaluation to a `TimingPolicy`:

```java
interface TimingPolicy {
    TimingStatus evaluate(Game game, TimeAccount account, long turnTime); // NONE, TIMEOUT_POSSIBLE, AUTO_TIMEOUT
    void onTurnEnd(Game game, TimeAccount account, long turnTime);        // drain bank/pool, add increment
    void onPeriodStart(Game game, TimeAccount home, TimeAccount away, PeriodType type); // half / overtime reset
}
```

  Implementations: `TurnTimingPolicy`, `TurnWithBankTimingPolicy`, `PoolTimingPolicy`, created by a
  `TimingPolicyFactory` from `TIMING_MODE`. Policies are pure (time passed in) for unit testing.
* Charging: `onTurnEnd` is called where `StepEndTurn` (bb2025 l.411) stops the turn timer, before
  `turnTime` is reset for the next turn. Half/overtime resets hook into the bb2025 half/overtime
  start (same place where turn data is reset).
* **Automatic timeout is triggered on the command thread**: when `ServerGameTimeTask` detects
  `AUTO_TIMEOUT` it only enqueues `InternalServerCommandEnforceTimeout(gameId)` via
  `ServerCommunication.handleCommand(InternalServerCommand)` (once per turn, guarded by a transient
  flag in `GameState`). `ServerCommunication.handleByGameState` already routes internal commands
  with a game id to `GameState.handleCommand` → current step.
* `AbstractStep.handleCommand` handles `INTERNAL_SERVER_ENFORCE_TIMEOUT` like
  `CLIENT_ILLEGAL_PROCEDURE` (shared private method), without the passive-coach check, re-validating
  the policy status on the command thread (the turn may have ended meanwhile), and then calls the
  same `onTimeoutEnforced()` hook. Part A therefore covers manual and automatic timeouts alike.

### 8.4 Dialogs while the clock is exhausted

With automatic timeouts a coach idling inside an own dialog would still block §5. For `POOL` and
`TURN_WITH_BANK` with `AUTO_TIMEOUT`, after `AUTO_TIMEOUT_GRACE` the pending acting-coach dialog is
answered with its default (decline re-roll / don't use skill). Implemented per dialog type through
a `DefaultDialogAnswerService` that synthesises the corresponding client command for the current
step; dialogs without a safe default are left alone. This is the optional extension of §5 and can
be shipped after the rest of Part B.

### 8.5 Model, protocol and client

* `TimeAccount` (remaining bank/pool in ms, exhausted flag) per team, stored in `TurnData` (already
  per team, serialised and observed) with new `ModelChangeId`s (`TURN_DATA_SET_TIME_REMAINING`,
  `TURN_DATA_SET_TIME_EXHAUSTED`), JSON options in `IJsonOption`, handled in
  `ModelChangeProcessor`. Persisted with the game, so restarts keep the accounts.
* `ServerCommandGameTime` gains optional home/away remaining values for live display (backwards
  compatible JSON, missing ⇒ unknown).
* `ReportTimeoutEnforced` gains `automatic` (bool, default false) and the client message
  (`TimeoutEnforcedMessage`) reads e.g. "Time bank of <coach> exhausted – turn ends."
* `TurnDiceStatusComponent`: show remaining bank/pool per coach; *Timeout* button only when the
  policy allows manual timeouts (`timeoutPossible`). `UtilClientTimeout` status text per mode.
* Game options dialog lists the new options via their `GameOptionFactory` messages.

## 9. Constraints

### 9.1 Ruleset scope

Accounting/policies are ruleset independent, but timeouts only take effect promptly in BB2025
(Part A). For bb2016/bb2020 games either reject non-`TURN` modes at game creation
(`UtilServerStartGame`) or accept that the timeout only applies at the next selection. Recommended:
restrict `TURN_WITH_BANK` / `POOL` to BB2025.

### 9.2 Compatibility

* Defaults reproduce today's behaviour; replays of old games are unaffected (new JSON fields optional).
* Disconnects: clock stops as today (`ServerCommandHandlerSocketClosed`), so banks don't drain
  while a coach is disconnected.

## 10. Open questions

1. Should an enforced timeout remain a voluntary end of turn (stalling roll applies) or become a
   turnover?
2. Should time spent by the passive coach on dialogs during the acting coach's turn be charged to
   the passive coach in `POOL` mode?
3. Should setup / kickoff phases be charged to bank/pool?
4. Exhausted pool: `AUTO_TIMEOUT` with minimum per-turn time vs. forfeit/concession?
5. Restrict new modes to BB2025 or support degraded enforcement in older rulesets?

## 11. Work packages

| # | Package | Depends on |
|---|---|---|
| A1 | `AbstractStep.onTimeoutEnforced` hook + hardening (§4.1, §6) | — |
| A2 | `StepInitSelecting` start/hook (§3) | A1 |
| A3 | bb2025 waiting-step overrides (§4.2) | A1 |
| A4 | `StepEndFeeding` cut-off (§5) | — |
| A5 | Statetests (§7) + change list entry (improvement: "An enforced timeout now ends the turn without waiting for the next player activation") | A1–A4 |
| B1 | Options (§8.2) | — |
| B2 | `TurnTimerService` + `TimingPolicy` implementations + unit tests | B1 |
| B3 | `TimeAccount` model/sync (§8.5) | B1 |
| B4 | Charging/reset hooks in bb2025 `StepEndTurn` / half start | B2, B3 |
| B5 | `InternalServerCommandEnforceTimeout` + tick detection (§8.3) | A1, B2 |
| B6 | Client display, report flag, options dialog | B3, B5 |
| B7 | Default dialog answers (§8.4) | B5 |
| B8 | Statetests for bank/pool exhaustion + change list entry (feature) | B4–B6 |

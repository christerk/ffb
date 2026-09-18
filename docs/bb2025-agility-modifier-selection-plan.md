# BB2025: Optional Agility Modifier Selection (all Agility Tests)

Status: dodge **implemented**, all other Agility Tests **planned / not implemented**.
Scope: **BB2025 only**. `bb2016` and `bb2020` keep their current behaviour untouched.

> This document supersedes `docs/bb2025-dodge-modifier-selection-plan.md`. The dodge design is
> retained here as Part A because it is the template every other Agility Test follows; Part B
> describes the generalisation to pick up, catch, jump, jump up, interception and Right Stuff.

---

## 1. Goal

Consummate Professional grants a once per game **+1 modifier to an Agility Test**, not just to a
dodge. The dodge implementation already provides everything needed — opt-in modifiers, a combined
"use a skill or a re-roll" dialog, minimum roll previews and a stalling aware auto re-roll — but all
of it is currently dodge shaped. The goal is to lift that machinery to a roll agnostic level and wire
every BB2025 Agility Test into it.

### 1.1 Rolls in scope

| Roll | Step | Optional modifiers | Skill re-roll | Diving Tackle | Failure is a turnover |
|---|---|---|---|---|---|
| Dodge | `step/bb2025/move/StepMoveDodge` | Break Tackle, Consummate Professional | Dodge (auto, stalling guarded) | yes | yes |
| Pick up | `step/bb2025/move/StepPickUp` | Consummate Professional | Sure Hands (auto, stalling guarded) | no | yes |
| Catch | `step/bb2025/shared/StepCatchScatterThrowIn` | Consummate Professional | Catch (auto only for deliberate deliveries, see §3.4) | no | sometimes |
| Jump | `step/bb2025/move/StepJump` | Consummate Professional; Leap, Very Long Legs, Pogo (conditionally, see §3.3) | Bounding Leap (auto, stalling guarded) | yes | yes |
| Jump up | `step/action/select/StepJumpUp` + `skillbehaviour/mixed/JumpUpBehaviour` | Consummate Professional | none | no | no (player stays prone) |
| Interception | `step/bb2025/pass/StepIntercept` | Consummate Professional | none | no | no |
| Right Stuff / landing | `step/bb2025/ttm/StepRightStuff` | Consummate Professional | Swoop | no | yes |

### 1.2 Explicitly out of scope

* **Hypnotic Gaze** and **Secure the Ball** — not Agility Tests.
  `mechanics/bb2025/AgilityMechanic.minimumRollHypnoticGaze` is a flat `3+` and stays that way.
* **Safe Throw** — `bb2016` only.
* All passing and throwing rolls.
* `bb2016` / `bb2020` behaviour of any kind.

---

# Part A — The implemented dodge mechanism (the template)

## 2. Opt-in modifiers

### 2.1 `DodgeContext` carries the selection

`ffb-common/src/main/java/com/fumbbl/ffb/modifiers/DodgeContext.java` holds a set of explicitly
selected skills (never null, defaults to empty) and exposes `isSkillSelected(Skill)`. The existing
4-arg and 5-arg (`boolean useBreakTackle`) constructors remain and delegate with an empty selection
set, so `bb2016`/`bb2020` are unaffected.

### 2.2 `DodgeModifier.optional`

`DodgeModifier` carries a `boolean optional` flag with `isOptional()`, defaulting to `false` in all
existing constructors. `useStrength` is kept, it is consumed by `mechanics/bb2016/AgilityMechanic`
and `ffb-client-logic/.../report/DodgeRollMessage.java`; `optional` is a new orthogonal flag.

### 2.3 BB2025 skills

`skill/bb2025/BreakTackle.java` registers its `-1`/`-2`/`-3` modifiers (by ST band) as optional and
gated on `context.isSkillSelected(skill)` — the old `|| UtilCards.hasUnusedSkill(...)` clause that
speculatively included Break Tackle everywhere is gone.

`skill/bb2025/special/ConsummateProfessional.java` (`SkillUsageType.ONCE_PER_GAME`) registers an
optional `-1 REGULAR` modifier with the same gate.

> Dodge modifiers are stored inverted: a `+1` bonus is `-1`.

### 2.4 `OptionalDodgeModifierService`

`ffb-common/src/main/java/com/fumbbl/ffb/modifiers/OptionalDodgeModifierService.java` (instantiable,
instance methods) answers "which optional modifiers could this player still use here":

```java
List<OptionalDodgeModifier> availableFor(Game game, ActingPlayer actingPlayer,
                                         FieldCoordinate from, FieldCoordinate to);
```

It walks the player's skills, keeps the `isOptional()` modifiers of unused skills, and probes the
modifier's own preconditions (e.g. the ST band) by building a context whose selection set contains
exactly that skill and checking whether the factory returns the modifier. It lives in `ffb-common`
because both the server selection service and the BB2025 `AgilityMechanic` preview need it.

### 2.5 `DodgeModifierSelectionService`

`ffb-server/src/main/java/com/fumbbl/ffb/server/util/bb2025/DodgeModifierSelectionService.java`
enumerates every non-empty subset of the available optional modifiers, evaluates each against the
actual die, and produces two lists:

* **`findOptions(...)` → actionable buttons.** Only subsets that *guarantee success* for the die that
  was rolled, with non-minimal subsets pruned (a smaller successful subset already exists).
* **`findCombinations(...)` → informational bullet list.** Every combination with the roll it would
  have required, so the coach can see what a re-roll could achieve.

Four pruning/ranking rules, applied in this order:

1. `findOptions` keeps only successful combinations and drops the non-minimal ones.
2. `removeRedundantSingleSkills` (inside `evaluateCombinations`, so it affects both lists): among
   *single skill* entries with the same `getTotalModifier()`, keep only those with the lowest
   `usageCost` — prefer the skill that can be used more often.
3. `removeRedundantSupersets` (only in `findCombinations`): drop a combination when a combination it
   contains already needs the same `minimumRoll`. Relies on the enumeration being ordered by
   ascending size.
4. `usageCost`: `REGULAR`/`ONCE_PER_TURN`/`ONCE_PER_TURN_BY_TEAM_MATE` → `0`, `ONCE_PER_DRIVE` → `1`,
   `ONCE_PER_HALF` → `2`, `ONCE_PER_GAME`/`SPECIAL` → `3`. The ordering deliberately lives on the
   service and not on the shared `SkillUsageType` enum.

Each entry is described for the dialog and the game log as `"Break Tackle"`,
`"Consummate Professional"`, `"Break Tackle + Consummate Professional"`.

### 2.6 `ModifierChoiceOption`

`ffb-common/src/main/java/com/fumbbl/ffb/model/ModifierChoiceOption.java` (`IJsonSerializable`):
`List<Skill> skills`, `int totalModifier`, `int minimumRoll`, `String label`. `minimumRoll` is the
roll that would have been required with this option's skills applied, including the Diving Tackle
`-2` when the option came from the DT look-ahead. Skills serialise by name and resolve through
`source.getRules().getSkillFactory()`.

### 2.7 The dialog

| Module | File |
|---|---|
| ffb-common | `dialog/DialogReRollModifierChoiceParameter.java`, `dialog/DialogId.RE_ROLL_MODIFIER_CHOICE`, `model/ModifierChoiceOption.java`, `net/commands/ClientCommandReRollModifierChoice.java` |
| ffb-client-logic | `client/dialog/DialogReRollModifierChoice.java`, `client/dialog/DialogReRollModifierChoiceHandler.java`, `DialogManager`, `ClientCommunication.sendReRollModifierChoice(...)` |

`DialogReRollModifierChoiceParameter` carries `playerId`, `reRolledAction`, the unmodified
`minimumRoll`, the die that was rolled, `fumble`, `modifierOptions` (buttons), `modifierCombinations`
(bullet list), the re-roll half (`reRollProperties` + `reRollSkill`, from `ReRollOptions` via
`mechanic/bb2025/RollMechanic.findReRollOptions`) and a free-form `messages` list.

The dialog is used for **every** BB2025 dodge re-roll prompt, including when there is no modifier
option at all (the option list is then empty) — a single code path.
`DialogReRollModifierChoiceHandler` sends `ClientCommandReRollModifierChoice` for a modifier pick and
the ordinary `ClientCommandUseReRoll` for a re-roll pick, so the re-roll branch needs no server side
changes. It routes by `game.getTeamHome().hasPlayer(player)`, so it already works for players that
are not the acting player.

### 2.8 `StepMoveDodge`

```
1. if (doRoll) roll and publish DODGE_ROLL
2. modifiers  = factory.findModifiers(new DodgeContext(..., selectedModifierSkills))   // empty at first
3. minimumRoll = mechanic.minimumRollDodge(...); successful = isSkillRollSuccessful(roll, minimumRoll)
4. report the roll (only when doRoll)
5. successful -> divingTackleLookAhead()          // §2.9
   otherwise  -> offerRescue(modifiers, minimumRoll, dueToDivingTackle = false)
```

`offerRescue(...)` merges the modifier options with the available re-roll sources into one dialog; if
neither exists the dodge simply fails. On a modifier reply the skills are added to
`selectedModifierSkills`, marked used **immediately** (all of the active player's modifiers must be
declared before the Diving Tackle decision), reported via
`ReportSkillUse(..., SkillUse.ADD_DODGE_MODIFIER)`, published as
`StepParameterKey.SELECTED_DODGE_MODIFIER_SKILLS`, and the step re-enters the evaluation without a new
die. `modifierChoiceOffered` suppresses a second prompt in the retry step.

### 2.9 Diving Tackle look-ahead

```
divingTacklers = UtilPlayer.findEligibleDivingTacklers(game, from, to, canAttemptToTackleDodgingPlayer)
if (none || modifierChoiceOffered) return SUCCESS
withDt = modifiers + forType(DIVING_TACKLE)
if (roll still succeeds with withDt) return SUCCESS     // DT step still prompts the defender
return offerRescue(withDt, minimumRollWithDt, dueToDivingTackle = true)
```

`skillbehaviour/bb2025/DivingTackleBehaviour` always prompts the defender, even when DT cannot change
the outcome, so the tackler can still be placed prone in the vacated square. It consumes nothing —
the active player's modifiers were committed by `StepMoveDodge`.

### 2.10 Automatic skill re-rolls and stalling

When no modifier option is available and the only rescue is a free skill re-roll (e.g. Dodge), the
step uses it without asking — **except** when failing would get a rock thrown at a team mate.
`step/bb2025/shared/StallingExtension.wouldEndOfTurnTriggerStallingRoll(game, player)` answers that:
the stalling check has to be enabled, it has to be a regular turn number 6 or lower, and a team mate
other than the acting player has to hold the ball, still be unactivated, and count as stalling. In
that case the coach gets the normal dialog instead, so they can decline and fail on purpose.

### 2.11 Move-square preview

`AgilityMechanic.minimumRollDodgePreview(game, actingPlayer, from, to)` is abstract per ruleset.
`bb2016`/`bb2020` replicate the old inline `UtilServerPlayerMove` computation verbatim; `bb2025`
applies every currently available optional modifier (best achievable roll) and adds the Diving Tackle
modifier when an eligible opponent could declare it. `UtilServerPlayerMove.addMoveSquare` is a pure
dispatch to the mechanic.

---

# Part B — Generalising to every Agility Test

## 3. Rules decisions

### 3.1 Unlimited-use modifiers stay automatic

`Extra Arms` (`skill/common/ExtraArms.java`: `-1` on pickup, interception and catch) and
`Diving Catch` (`skill/common/DivingCatch.java`: `-1` on catch, gated to an accurate pass or bomb)
may be used any number of times per turn. They are auto-applied today and stay that way — making
them optional would only add clicks. The same holds for Very Long Legs' `-2` interception modifier.

This is also strictly safe: the modifier dialog is only reached when the roll has already failed
*with* those modifiers applied.

### 3.2 Limited-use modifiers are optional

Consummate Professional (`ONCE_PER_GAME`) and Break Tackle are optional because spending them has a
real cost. Consummate Professional must be registered for **every** roll in §1.1.

### 3.3 Jump modifiers are conditionally optional

`Leap` (`skill/bb2025/Leap.java`, `JumpModifier(-1, DEPENDS_ON_SUM_OF_OTHERS)`, self-gated on the
accumulated modifiers), `Very Long Legs` (`skill/bb2025/VeryLongLegs.java`,
`JumpModifier(-1, REGULAR)`) and `Pogo` (`skill/bb2025/Pogo.java`) are free and unlimited, so on
their own they should just be applied.

The one reason to decline them is the reason given for this change: a coach may want to **fail the
jump on purpose** so that the turn ends by turnover instead of voluntarily, avoiding a stalling roll
for a team mate. They are therefore **conditionally optional**: auto-applied normally, and only
surfaced as declinable options when
`StallingExtension.wouldEndOfTurnTriggerStallingRoll(game, actingPlayer)` is true — exactly the
condition that already guards the automatic dodge re-roll (§2.10).

Notes:

* `Pogo` is not a modifier, it is a pair of properties (`ignoreTacklezonesWhenJumping` and
  `CancelSkillProperty(makesJumpingHarder)`) read by `factory/mixed/JumpModifierFactory`. Since
  tacklezones and Prehensile Tail are the only negative jump modifiers in BB2025, those two
  properties *are* "ignores all modifiers on a Jump". Declining them therefore needs a distinct
  option kind ("do not ignore jump modifiers") rather than a skill selection, and the factory has to
  read a flag off the context instead of the property directly.
* `Pogo`'s third property, `CancelSkillProperty(canAttemptToTackleJumpingPlayer)` (Diving Tackle
  immunity), is a separate rules effect and stays unconditional.
* `Leap` is `DEPENDS_ON_SUM_OF_OTHERS` and **mutates** `JumpContext` (`addModifierValue`), so its
  availability must be evaluated against the post-selection context, not the initial one.

### 3.4 The Catch skill re-roll is optional on a loose ball

`skillbehaviour/bb2025/CatchBehaviour` currently sets `state.rerollCatch = true` whenever the catcher
has Catch, and `StepCatchScatterThrowIn` then re-rolls immediately, for every catch mode.

For a **bouncing ball or an inaccurate/fumbled pass** the Catch re-roll must **always** be offered as
a choice and never used automatically — the coach may prefer not to hold the ball in that square, or
may want the turnover. Mechanically these all reduce to `CatchScatterThrowInMode.CATCH_SCATTER`:
`CATCH_MISSED_PASS` converts to `CATCH_SCATTER` (`StepCatchScatterThrowIn:329-335`), a fumble sets
`SCATTER_BALL` (`skillbehaviour/bb2025/PassBehaviour:203-205`) which bounces into `CATCH_SCATTER`
(`StepCatchScatterThrowIn:700,744`). The bomb equivalents must be checked during implementation.

For a deliberate delivery (`CATCH_ACCURATE_PASS`, `CATCH_HAND_OFF`, `CATCH_PUNT`, `CATCH_KICKOFF`)
the re-roll keeps being used automatically, subject to the usual stalling guard of §2.10.

### 3.5 `CHECK_FORGO` stays voluntary-end-turn only

Confirmed: the stalling roll is only triggered when the coach ends the turn voluntarily, never on a
turnover. That asymmetry is intended and is precisely what makes a deliberate failure attractive, so
the guards in §2.10 and §3.3 are the right mechanism.

---

## 4. Phase 1 — shared groundwork

No behaviour change: only dodge reaches the selection service until Phase 2 starts.

1. **Lift `optional` from `DodgeModifier` onto `RollModifier`**, so `CatchModifier`,
   `PickupModifier`, `JumpModifier`, `JumpUpModifier`, `InterceptionModifier` and
   `RightStuffModifier` all carry it. `DodgeModifier` keeps its current constructors.
2. **Lift the selection set from `DodgeContext` to a shared carrier** — a small holder used by
   `ModifierContext` (or default methods on it) exposing `isSkillSelected(Skill)` — and expose it on
   every agility context. `DodgeContext` delegates to it, so existing dodge code is untouched.
3. **Introduce an agility roll descriptor**, e.g. an enum
   `AgilityRoll { DODGE, PICK_UP, CATCH, JUMP, JUMP_UP, INTERCEPTION, RIGHT_STUFF }` carrying per
   roll: the `ReRolledActions` value, the modifier factory type, a context factory and the minimum
   roll function. This is what stops the selection service from being dodge shaped.
4. **Generalise the selection service**: `DodgeModifierSelectionService` →
   `AgilityModifierSelectionService` (same package `server/util/bb2025`), parameterised by the
   descriptor. All four rules of §2.5 stay; the existing 13 tests are re-targeted at the dodge
   descriptor and must stay green.
   * **Critical:** `JumpContext` is mutable and `Leap` is `DEPENDS_ON_SUM_OF_OTHERS`, so the service
     must build a **fresh context per candidate combination**. Its API changes from "takes a context"
     to "takes a `Supplier<ModifierContext>`".
5. **Generalise `OptionalDodgeModifier` / `OptionalDodgeModifierService`** to `OptionalRollModifier` /
   `OptionalRollModifierService` operating on `RollModifier`.
6. **Rename the roll specific names**: `StepParameterKey.SELECTED_DODGE_MODIFIER_SKILLS` →
   `SELECTED_AGILITY_MODIFIER_SKILLS`, `SkillUse.ADD_DODGE_MODIFIER` → `ADD_AGILITY_MODIFIER` with
   roll neutral text. Check the serialisation ids for backwards compatibility.
7. **Register Consummate Professional for every agility roll**: replace its single `DodgeModifier`
   with one optional `-1 REGULAR` modifier per roll type, each gated on `context.isSkillSelected`.
   Usage stays `ONCE_PER_GAME`.

## 5. Phase 2 — per action work packages

Each package follows §2.8: roll → evaluate → on failure ask the selection service for options and
combinations → merge with the available re-roll sources into one
`DialogReRollModifierChoiceParameter` → apply the reply (fresh context + re-evaluate, or re-roll) →
auto-use a lone free re-roll only when §2.10 allows it.

### A. Pick up — `step/bb2025/move/StepPickUp`

The acting player rolls, so `ActingPlayer.markSkillUsed` applies. Fold the existing automatic Sure
Hands branch into the merged dialog: when Consummate Professional is also available, Sure Hands must
be offered next to it instead of being consumed silently. Keep the automatic use when no modifier
option exists.

### B. Catch — `step/bb2025/shared/StepCatchScatterThrowIn`

The catcher is usually **not** the acting player: use `ReRollRequest.forPlayer(...)` and
`Player.markUsed` / `isUsed` (honoured for `SkillUsageType.isTrackOutsideActivation()`), not
`ActingPlayer.markSkillUsed`. The Catch re-roll arrives through the `CatchBehaviour` step hook, so
the hook must publish its re-roll source into the merged dialog instead of firing `state.rerollCatch`
directly, and §3.4 decides whether it may be auto-used. The step can move the ball within a single
invocation, so the final catcher must be resolved from the current `FieldModel` coordinates.

### C. Interception — `step/bb2025/pass/StepIntercept`

The interceptor is an opposing player and there is no BB2025 re-roll source, so the dialog carries
options only and an empty re-roll list. It must be shown to the interceptor's coach; the existing
handler already routes by team, so no handler change is needed.

### D. Right Stuff / landing — `step/bb2025/ttm/StepRightStuff`

The thrown player is not the acting player. Merge the `Swoop` re-roll
(`SkillUsageType.ONCE_PER_TURN_BY_TEAM_MATE`, registered as a re-roll source for
`ReRolledActions.RIGHT_STUFF`) with Consummate Professional. `fumbledPlayerLandsSafely` keeps its
automatic success; the dialog only appears on a genuinely failed landing roll. Note that BB2025 has
no skill based Right Stuff modifiers at all today, only throw quality and tacklezones.

### E. Jump — `step/bb2025/move/StepJump`

The richest case.

* Implement §3.3: keep `Leap`, `Very Long Legs` and `Pogo` auto-applied inside the jump modifier
  factory, and only when the stalling condition holds build the modifier set **without** them and
  offer them as declinable options. `Pogo` needs the distinct "do not ignore jump modifiers" option
  kind driven by a context flag.
* Evaluate `Leap`'s availability against the post-selection context.
* Merge with the automatic Bounding Leap re-roll, applying the stalling guard.
* Port the Diving Tackle look-ahead from `StepMoveDodge` (§2.9).
* **Ruleset separation:** `modifiers/mixed/JumpModifierCollection` and `factory/mixed/JumpModifierFactory`
  are shared with BB2020. Per the repository rules, duplicate them into `modifiers/bb2025` and
  `factory/bb2025` with retargeted `@RulesCollection` annotations rather than adding 2025-only
  behaviour to the shared classes, and never inherit a bb2025 class from a mixed/bb2020 one.

### F. Jump up — `step/action/select/StepJumpUp` + `skillbehaviour/mixed/JumpUpBehaviour`

`StepJumpUp` is `@RulesCollection COMMON` and the roll lives in the mixed behaviour. Duplicate both
into bb2025 packages (`step/bb2025/action/select/StepJumpUp`, `skillbehaviour/bb2025/JumpUpBehaviour`)
and add the dialog there. No re-roll source and no turnover, so this is the simplest case: options
only, stalling guard irrelevant.

## 6. Phase 3 — previews and client

1. Generalise `minimumRollDodgePreview` into a per roll preview that accounts for the best free
   optional modifiers, and **add a jump preview**: `UtilServerPlayerMove` computes jump squares
   inline and never consults the preview, so jump move squares currently under-report their
   difficulty.
2. Drive the dialog title and prompt from the roll descriptor; the dialog itself is already action
   agnostic, so this should be text only.
3. Check `DialogManager` / `TurnDiceStatusComponent` behaviour for dialogs raised for the **non
   acting** coach (catch, interception, Right Stuff).

## 7. Testing

* `AgilityModifierSelectionServiceTest` per roll type, with emphasis on jump (fresh context per
  combination, `Leap`'s `DEPENDS_ON_SUM_OF_OTHERS` gate, the `Pogo` option). The existing 13 dodge
  cases must keep passing after the rename.
* `AgilityMechanic` preview tests for jump, including `Pogo`/`Leap`/`Very Long Legs` and the Diving
  Tackle look-ahead, plus the existing `bb2016`/`bb2020` regression guards.
* JSON round trips for any changed dialog parameter, command and step state (replay and reconnect).
* `ffb-statetest` step flow regressions per action, following `ffb-statetest/TESTING_INSTRUCTIONS.md`
  (GameStateBuilder / StepEngine / Commands / TestRolls):
  * Consummate Professional offered after a failed roll, and declined.
  * Consummate Professional combined with the action's skill re-roll.
  * Automatic re-roll suppressed when a team mate would be considered stalling.
  * Catch re-roll always offered on a bouncing ball / inaccurate / fumbled pass, still automatic on an
    accurate pass, hand-off, punt or kickoff (§3.4).
  * Jump modifiers offered as declinable only under the stalling condition (§3.3).
  * Dialog routed to the correct coach for catch, interception and Right Stuff.
* Full `mvn -B install` before each progress report. Build order: `ffb-common` → `ffb-tools` →
  `ffb-server` → `ffb-client-logic` → `ffb-resources` → `ffb-client` → `ffb-statetest`.

## 8. Change list

One user facing entry per newly covered action, or a single combined entry such as *"Consummate
Professional and other optional modifiers can now be used on all Agility Tests"*, added to the top
`VersionChangeList` in `ffb-client-logic/.../client/model/ChangeList.java` via `addFeature`, **without**
naming the rules version. The Catch re-roll change of §3.4 is a behaviour change to released
behaviour and needs its own entry. No entries for defects introduced by this work itself.

## 9. Sequencing

| # | Package | Modules |
|---|---|---|
| 1 | Phase 1 shared groundwork (§4) | ffb-common, ffb-server |
| 2 | F — jump up (simplest, proves the generalisation end to end) | ffb-server |
| 3 | A — pick up | ffb-server |
| 4 | C — interception | ffb-server |
| 5 | D — Right Stuff | ffb-server |
| 6 | B — catch, including the §3.4 re-roll change | ffb-server |
| 7 | E — jump, including the bb2025 jump factory/collection split | ffb-common, ffb-server |
| 8 | Phase 3 previews and client (§6) | ffb-common, ffb-server, ffb-client-logic |
| 9 | Change list (§8) | ffb-client-logic |

Report progress after each package.

## 10. Open items

* The bomb catch modes (`CATCH_BOMB`, `DEFLECTED_BOMB`) and `CATCH_THROW_IN` need a ruling on whether
  they count as "loose ball" for §3.4; the plan currently treats only the modes that reduce to
  `CATCH_SCATTER` as always optional.
* Whether the "do not ignore jump modifiers" option for `Pogo` should be presented as a single
  combined option together with `Leap`/`Very Long Legs` or as separate toggles.

# BB2025: Chained Armour and Injury Modifications

Status: **planned / not implemented**.
Scope: **BB2025 only**. `bb2016` and `bb2020` keep the current "one precomputed alternative" behaviour untouched.

---

## 1. Goal

Skills that modify an armour or injury roll after it was made (Crushing Blow, Dwarfen Grit, Krump and Smash,
Old Pro, Savage Mauling, Slayer, Toxin Connoisseur, ...) are currently limited to a single modification per
injury. The goal is to allow arbitrarily long chains of such modifications, with new options appearing whenever
a previous decision changed the result.

### 1.1 Rules (confirmed)

1. **Chains of any length per side.** After every accepted modification the result is re-evaluated and the set
   of usable skills is determined anew. A modification can make other skills usable, e.g. Crushing Blow breaks
   the armour, afterwards Dwarfen Grit becomes usable for the defender.
2. **Each roll is re-rolled at most once.** Once the armour roll was re-rolled (by any skill of any side), no
   further armour re-roll is offered. The same applies separately to the injury roll. Example: Krump and Smash
   re-rolled the armour and broke it, Dwarfen Grit is no longer available.
   Modifier skills (adding armour or injury modifiers) are not limited by this rule.
3. **Exactly one coach is asked per state.**
   * Armour phase, armour **not** broken: coach of the attacker (active or passive, e.g. Skull).
     Without an attacker nobody is asked.
   * Armour phase, armour broken: coach of the defender.
   * Injury phase: coach of the attacker; without an attacker the coach of the defender.
   After every decision the state is re-evaluated, which determines both the usable skills and the coach asked.
4. **Ordering of the offered skills**
   1. Usage frequency, more frequently usable first:
      `REGULAR`, `ONCE_PER_TURN`, `ONCE_PER_DRIVE`, `ONCE_PER_HALF`, `ONCE_PER_GAME`.
      `SPECIAL` and `ONCE_PER_TURN_BY_TEAM_MATE` are not relevant for these skills and are ranked like
      `ONCE_PER_TURN`.
   2. Restriction, most restricted first: stat restriction (e.g. only against ST5+), keyword restriction
      (e.g. only against Big Guys), no restriction.
   3. Skill name as deterministic tie breaker.

### 1.2 Out of scope

* `bb2016` / `bb2020` behaviour of any kind.
* Regular armour / injury modifiers resolved by the injury types themselves (Mighty Blow, Claws, Chainsaw,
  Stunty, Niggling Injuries, foul assists, ...). They stay part of the base evaluation.
* Casualty roll modifications.

---

## 2. Current mechanism (summary)

| Part | Role today |
|---|---|
| `server/injury/injuryType/ModificationAwareInjuryTypeServer` | `final handleInjury`: armour roll, at most one attacker modification, defender modification only if the attacker did not modify and armour is broken, injury roll on both contexts. |
| `server/injury/modification/InjuryContextModification` | Template per skill: `tryArmourRollModification`, `prepareArmourParams`, `armourModificationCantHelp`, `applyArmourModification`, `tryInjuryModification`, `modifyInjuryInternal`, valid injury types, `appliesToDefender`, `requiresConditionalReRollSkill`. Copies the context into a `ModifiedInjuryContext`. |
| `injury/context/InjuryContext` + `ModifiedInjuryContext` | One slot for one precomputed alternative, nesting is explicitly prevented. |
| `server/model/SkillBehaviour.registerModifier` / `model/Player.getUnusedInjuryModification` | One modification per skill, `findFirst()` per player. |
| `server/step/bb2025/shared/StepHandleDropPlayerContext` | One `DialogSkillUse`, optional `PRO` step, `InjuryResult.swapToAlternateContext`. |
| `server/util/UtilServerInjury.handleInjury` | Evaluates both contexts up front (SI, apothecary, stun to KO). |
| `server/mechanic/bb2025/StateMechanic.reportInjury` | Derives the report parts to skip from `InjuryModification` (`ARMOUR` / `INJURY`). |
| `server/step/bb2025/block/StepBlockChainsaw` | Precomputes `modifiedInjuryEndsTurn` for the single alternative. |

Limitations this plan removes: one alternative only, one modification per player, attacker and defender
modifications exclude each other, the armour branch discards injury modifications, dice of declined
modifications are rolled anyway (Old Pro), reporting and turnover logic assume one modification level.

---

## 3. Design

### 3.1 State = dice + ordered list of applied modifications

A new BB2025 injury resolution state replaces the precomputed alternative:

* the original armour and injury dice,
* an ordered `List<AppliedInjuryModification>` with skill, phase, effect, replaced dice and reports,
* `armourReRolled` and `injuryReRolled` flags,
* the current phase (`ARMOUR`, `INJURY`, `DONE`).

The result is always **recomputed** from that state:

1. Base evaluation of the injury type (`armourRoll(..., roll = false)` / injury interpretation) on the current
   dice.
2. Applying all accepted modifications in order.

**Pitfall:** `InjuryTypeBlock.armourRoll` (and other injury types, 5 occurrences in `injuryType/`) call
`clearArmorModifiers()`. The modifiers added by accepted modifications must therefore be kept separate from the
base modifiers and be re-added after the base evaluation, otherwise e.g. Crushing Blow would be lost when Dwarfen
Grit re-rolls the armour.

### 3.2 Self-describing modifications

`InjuryContextModification` is duplicated into a BB2025 variant (no inheritance between ruleset classes, see
`AGENTS.md`), e.g. `server/injury/modification/bb2025/ChainableInjuryModification`, exposing:

| Method | Purpose |
|---|---|
| `Set<Phase> phases()` | `ARMOUR`, `INJURY` or both (Ram / Dwarven Scourge / Slayer). |
| `Effect effect()` | `MODIFIER` or `RE_ROLL`. Re-roll: Krump and Smash, Lone Fouler, Master Assassin, Dwarfen Grit, Old Pro (single armour die), Savage Mauling (injury). Modifier: Crushing Blow, Toxin Connoisseur, Ram / Dwarven Scourge, Slayer. |
| `Restriction restriction()` | `STAT`, `KEYWORD`, `NONE`. Ideally declared as a restriction object (e.g. `KeywordRestriction(BIG_GUY)`) that is used both for the applicability check and for ranking, so both can not diverge. |
| `boolean isApplicable(state, phase)` | Side effect free check, derived from today's `try*`, `armourModificationCantHelp`, team checks and `ignoresArmourModifiersFromSkills`. Includes "does it help its owner in the current state". |
| `void apply(state)` | Changes dice or adds modifiers, rolls dice only here. |
| `boolean requiresConditionalReRollSkill()` | Unchanged (Old Pro → `PRO` step). |

Owner side is derived from the player owning the skill (attacker or defender), replacing `appliesToDefender`.

### 3.3 Collecting candidates

* `SkillBehaviour` keeps a list of registered modifications (`registerModifier` adds instead of replacing).
* `Player.getUnusedInjuryModifications(InjuryType)` returns all unused ones.
* New service class (instance methods, see `AGENTS.md`) `server/injury/modification/bb2025/InjuryModificationCandidateService`:
  `List<Candidate> candidates(Game, InjuryResolutionState)`:
  1. Determine the coach to ask (rule 3). None → empty list.
  2. Collect unused modifications of attacker and defender that belong to that coach.
  3. Filter by phase, valid injury type, `isApplicable`, re-roll flags.
  4. Sort with `InjuryModificationOrderingService` (rule 4).

### 3.4 Ordering

`InjuryModificationOrderingService` provides a `Comparator<Candidate>`: usage rank, restriction rank, skill name.

`AgilityModifierSelectionService.usageCost` is **not** reused as is: it ranks `REGULAR` and `ONCE_PER_TURN`
equally and `SPECIAL` like `ONCE_PER_GAME`, both differ from rule 4.

### 3.5 Phased injury types

`ModificationAwareInjuryTypeServer.handleInjury` stays for `bb2016` / `bb2020`. For BB2025 the injury types
additionally expose the phases separately so that the step can drive them:

* `rollArmour(...)` – initial armour roll and base evaluation,
* `evaluateArmour(state)` – base evaluation on current dice without rolling,
* `rollInjury(...)` / `evaluateInjury(state)` – only once the armour phase is finished and armour is broken.

The injury roll is only made after all armour decisions are made. Which variant is used is selected through the
BB2025 mechanic (e.g. `InjuryMechanic`), not through inheritance between ruleset classes.

### 3.6 Decision loop in `step/bb2025/shared/StepHandleDropPlayerContext`

```
ARMOUR phase:
  loop:
    candidates = candidateService.candidates(game, state)
    none → finish phase
    dialog for the asked coach (one candidate → DialogSkillUse, several → selection dialog, sorted)
      accepted → (PRO step if required) → apply, markUsed, set re-roll flag, add reports,
                 re-evaluate, continue loop
      declined → finish phase
armour broken → roll injury → INJURY phase with the same loop
DONE → evaluateInjuryContext (SI, apothecary, stun to KO), turnover decision, dropPlayer
```

* A decline ends the phase: the state is unchanged, so the asked coach and the candidates would be the same.
* Every acceptance consumes a skill, the loop terminates.
* A failed `PRO` roll: the skill is not applied, Pro counts as used, the loop continues with the unchanged state
  (Old Pro is no longer a candidate, other candidates of the same coach may be offered).
* Old Pro must only roll its die after a successful `PRO` roll.

Example: armour holds → attacker accepts Crushing Blow → armour broken → defender accepts Dwarfen Grit
(re-roll, `armourReRolled`) → armour holds → attacker: Krump and Smash blocked, only modifier skills are offered
→ none → phase finished.

### 3.7 Dialog

* Single candidate: existing `DialogSkillUseParameter` (unchanged client behaviour).
* Several candidates: a selection dialog listing the sorted skills plus "none". Check whether
  `DialogSelectSkillParameter` fits, otherwise add a dedicated parameter. The client must keep the order sent by
  the server.
* `handleCommand` only accepts answers from the asked coach for skills in the current candidate list (same
  hardening as in the agility modifier selection).

### 3.8 Reporting

Reports are written incrementally instead of derived after the fact:

1. Armour roll before the first decision.
2. Per accepted modification: `ReportSkillUse`, skill specific reports (e.g. `ReportOldPro`), new armour / injury
   result.
3. Injury roll at the start of the injury phase, casualty parts after `DONE`.

The step tells `StateMechanic.reportInjury` (bb2025) which parts to report; the skip derivation from
`InjuryModification` is no longer used for BB2025. Client log rendering has to be checked for repeated armour /
injury results within one injury.

### 3.9 Moving evaluation and turnover to the end

* `UtilServerInjury.handleInjury` must no longer evaluate a modified context for BB2025, evaluation happens once
  the loop reaches `DONE`.
* `modifiedInjuryEndsTurn` (`DropPlayerContext`, `DropPlayerContextBuilder`, precomputed in
  `step/bb2025/block/StepBlockChainsaw`) is replaced by a turnover decision computed from the final result.
* All BB2025 producers of a `DropPlayerContext` have to be checked for logic reading the injury result before the
  decisions: `StepBlockChainsaw`, `StepBreatheFire`, `StepSteadyFooting`, `StepDropFallingPlayers`,
  `StepThrowARock`, `StallingExtension`, `StepRightStuff`, `StepSpecialEffect`, `StepInitScatterPlayer`,
  `AnimalSavageryBehaviour`, plus BB2025 steps calling `UtilServerInjury.handleInjury` directly.

### 3.10 Serialisation

* The resolution state (dice, applied modifications, flags, phase) is part of the step JSON / injury result JSON.
* `initFrom` must still read the old `alternateContext` format for games saved mid step; it is mapped to the old
  single decision behaviour or converted to a one element history.

---

## 4. Implementation steps

1. **Multiple registrations, no behaviour change.** List in `SkillBehaviour`, `getUnusedInjuryModifications`,
   existing code keeps using the first entry.
2. **BB2025 modification variant.** Duplicate into `modification/bb2025`, split check and apply, add `phases`,
   `effect`, `restriction`. Move all BB2025 behaviours to the new classes. Unit tests per modification for
   `isApplicable` / `apply`.
3. **Resolution state and recomputation.** State class, separation of base and skill modifiers, phased
   evaluation in the injury types, JSON incl. backwards compatibility.
4. **Candidate and ordering services** with unit tests (coach selection, re-roll limit, ordering).
5. **Decision loop** in the BB2025 `StepHandleDropPlayerContext`, `PRO` integration, command hardening,
   selection dialog (server and client).
6. **Reporting** in the BB2025 `StateMechanic` and client log.
7. **Callers**: turnover and evaluation at the end, remove `modifiedInjuryEndsTurn` usage for BB2025.
8. **Change list** entry in the latest `VersionChangeList` (`ChangeList.java`), without naming the rules
   version.

## 5. Tests (`ffb-statetest`)

* Crushing Blow breaks armour → Dwarfen Grit offered to the defender → re-roll keeps the Crushing Blow modifier.
* Krump and Smash re-roll breaks armour → Dwarfen Grit **not** offered.
* Dwarfen Grit re-roll → armour holds → attacker is offered only modifier skills, no re-roll.
* Decline ends the phase, the same coach is not asked again for the same state.
* Ordering: once per turn before once per half; keyword restricted (Slayer) before unrestricted at the same usage
  rank.
* Ram / Dwarven Scourge: armour modifier declined, injury modifier offered in the injury phase.
* Old Pro: failed `PRO` roll → no die rolled, other candidates still offered; self inflicted case (no attacker)
  only asks the defender's coach when the armour is broken.
* Injury phase: Toxin Connoisseur / Savage Mauling offered to the attacker's coach; Savage Mauling injury re-roll
  only once.
* Team mate block (same coach for both players) and Skull (attacker is the passive player).
* Saved game JSON with the old `alternateContext` format still loads and resolves.
* `bb2020` regression: existing single alternative behaviour unchanged.

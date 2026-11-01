# BB2025: Chained Armour and Injury Modifications

Status: **in progress**, steps 1, 2 and 4 implemented in #351 (not used by the game flow yet).
Scope: **BB2025 only**. `bb2016` and `bb2020` keep the current "one precomputed alternative" behaviour untouched.

---

## 1. Goal

Skills that modify an armour or injury roll after it was made (Crushing Blow, Dwarfen Grit, Krump and Smash,
Old Pro, Savage Mauling, Slayer, Toxin Connoisseur, ...) are currently limited to a single modification per
injury. The goal is to allow arbitrarily long chains of such modifications, with new options appearing whenever
a previous decision changed the result.

Additionally, regular modifier skills that are applied automatically today (Mighty Blow, Claws, Dirty Player,
A Sneaky Pair, Lethal Flight, Arm Bar)
can be made optional per game option. When their option is enabled they are no longer part of the base
evaluation but take part in the decision chain like any other modification (see 3.11).

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
     Without an attacker nobody is asked. For injuries without an attacker that are caused by a player's skill
     (Arm Bar on a failed dodge / jump) that player counts as the attacker, see 3.11.
   * Armour phase, armour broken: coach of the defender.
   * Injury phase: coach of the attacker; without an attacker the coach of the defender.
   After every decision the state is re-evaluated, which determines both the usable skills and the coach asked.
   Exception: if the armour is broken and the defender has nothing to offer or declined, the attacker is asked
   for modifications flagged with `offeredOnBrokenArmour()` (Crushing Blow moving Mighty Blow from the armour to
   the injury roll). Injuries from a player's own Chainsaw / vomit-like attack have no attacker, the defender
   takes the attacker's role then (as today).
4. **Ordering of the offered skills**
   1. Usage frequency, more frequently usable first:
      `REGULAR`, `ONCE_PER_TURN`, `ONCE_PER_DRIVE`, `ONCE_PER_HALF`, `ONCE_PER_GAME`.
      `SPECIAL` and `ONCE_PER_TURN_BY_TEAM_MATE` are not relevant for these skills and are ranked like
      `ONCE_PER_TURN`.
   2. Restriction, most restricted first: stat restriction (e.g. only against ST5+), keyword restriction
      (e.g. only against Big Guys), no restriction.
   3. Skill name as deterministic tie breaker.
5. **Optional regular skills (game option dependent).** Mighty Blow, Claws, Dirty Player, A Sneaky Pair,
   Lethal Flight and Arm Bar each have a boolean game option. Option disabled (default): the skill is applied
   automatically by the base evaluation exactly as today. Option enabled: the skill is offered to its owner's
   coach as a `MODIFIER` candidate instead.
   * Their existing restrictions still apply:
     * armour **or** injury, never both: Mighty Blow, Dirty Player, A Sneaky Pair, Lethal Flight, Arm Bar;
     * Claws: armour only, only against AV 9+, cancelled by Iron Hard Skin;
     * Mighty Blow: blocks (and bombs with `BOMB_USES_MB`), not with Stab, Chainsaw, vomit like attacks or when
       distracted; Dirty Player: fouls only; A Sneaky Pair: fouls and Stab, only while the partner marks the
       defender; Lethal Flight: thrown player landing on an opponent; Arm Bar: failed dodge / jump out of the
       Arm Bar player's tackle zone (incl. Diving Tackle / Shadowing square);
     * `CLAW_DOES_NOT_STACK`, `MB_STACKS_AGAINST_CHAINSAW`, `BOMB_USES_MB`, `InjuryTypeBlock.Mode` and
       `ignoresArmourModifiersFromSkills` keep their meaning.
   * They are only offered when applying them changes the current result (armour broken / injury result),
     i.e. they are never offered just to be shown in the log.
   * Being `REGULAR` skills they are not marked as used; "already used for this injury" is derived from the
     list of applied modifications.

### 1.2 Out of scope

* `bb2016` / `bb2020` behaviour of any kind.
* Regular armour / injury modifiers resolved by the injury types themselves (Chainsaw, Stunty, Niggling
  Injuries, foul assists, ...). They stay part of the base evaluation. The skills of rule 5 stay part of it only
  while their game option is disabled.
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
| `skill/bb2025/MightyBlow`, `skill/mixed/Claws`, `skill/bb2025/DirtyPlayer`, `skill/bb2025/special/ASneakyPair`, `skill/bb2025/LethalFlight` | Register static / variable armour and injury modifiers. "Armour or injury" is encoded in the injury modifier's `appliesToContext` (no armour modifier with `affectsEitherArmourOrInjuryOnBlock` / `...OnFoul` / `...WithPartner` / `...OnTtm` present). |
| `skill/mixed/ArmBar` | Registers +1 armour / injury modifiers whose `appliesToContext` always returns `false`; the injury types add them directly. Properties `affectsEitherArmourOrInjuryOnDodge` / `...OnJump`. |
| `server/injury/injuryType/InjuryTypeFoul`, `InjuryTypeFoulForSpp`, `InjuryTypeStab`, `InjuryTypeStabForSpp` | A Sneaky Pair is removed from the skill armour modifiers and only added if the armour still holds. |
| `server/injury/injuryType/InjuryTypeTTMHitPlayerForSpp` | Lethal Flight added to the armour if it holds, otherwise to the injury. |
| `server/injury/injuryType/InjuryTypeDropDodge`, `InjuryTypeDropDodgeForSpp`, `InjuryTypeDropJump` | Arm Bar added to the armour if it holds, otherwise to the injury; the Arm Bar player is found via adjacent players / Diving Tackle square. |
| `server/step/bb2025/move/StepMoveDodge` | Collects Arm Bar players before the injury; several → `DialogPlayerChoiceParameter(ARM_BAR)` to pick the player receiving SPP, who becomes the attacker of `InjuryTypeDropDodgeForSpp`. |
| `server/injury/injuryType/InjuryTypeBlock` / `InjuryTypeFoul` (+ `Bomb*`, `*ForSpp`) | Apply those modifiers automatically: base roll first, skill armour modifiers only if the armour holds, Claws first with `CLAW_DOES_NOT_STACK` fallback, Mighty Blow only if Chainsaw alone does not break (`handleChainsawAndMb`), team mate `Mode` filtering. `PilingOn*` and `InjuryTypeFumbledKtm` are BB2016 / BB2020 only; BB2025's `InjuryTypeFumbledKtmApoKo` has no attacker. |
| `server/injury/modification/CrushingBlowModification` | Special case: if Mighty Blow broke the armour, Crushing Blow strips it from the armour so Mighty Blow moves to the injury roll. |
| `client/report/bb2025/InjuryMessage` | Prints the Claws line from the armour modifiers of the report. |

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
`AGENTS.md`). Implemented in `server/injury/modification/bb2025/chain`: `ChainableInjuryModification` plus one
`*ChainModification` class per skill (suffix avoids clashes with the legacy BB2025 classes), exposing:

| Method | Purpose |
|---|---|
| `Set<Phase> phases()` | `ARMOUR`, `INJURY` or both (Ram / Dwarven Scourge / Slayer). |
| `Effect effect()` | `MODIFIER` or `RE_ROLL`. Re-roll: Krump and Smash, Lone Fouler, Master Assassin, Dwarfen Grit, Old Pro (single armour die), Savage Mauling (injury). Modifier: Crushing Blow, Toxin Connoisseur, Ram / Dwarven Scourge, Slayer. |
| `Restriction restriction()` | `STAT`, `KEYWORD`, `NONE`. Ideally declared as a restriction object (e.g. `KeywordRestriction(BIG_GUY)`) that is used both for the applicability check and for ranking, so both can not diverge. |
| `boolean isApplicable(state, phase)` | Side effect free check, derived from today's `try*`, `armourModificationCantHelp`, team checks and `ignoresArmourModifiersFromSkills`. Includes "does it help its owner in the current state". |
| `void apply(state)` | Changes dice or adds modifiers, rolls dice only here. |
| `boolean requiresConditionalReRollSkill()` | Unchanged (Old Pro → `PRO` step). |
| `boolean offeredOnBrokenArmour()` | Attacker may be asked although the armour is broken (Crushing Blow, see rule 3). |
| `String ownerId(InjuryContext)` | Player who has to own the skill. |

Evaluation goes through the injectable `InjuryEvaluationService` (armour broken / injury interpretation) and
`InjuryContextCopyService` (side effect free simulations), so modifications can be unit tested with mocks.
BB2025 behaviours register the chain variants via `SkillBehaviour.registerModifier(ChainableInjuryModification)`
in addition to the legacy modification; the former `mixed` Old Pro, Crushing Blow, Ram and Savage Mauling
behaviours are split into `bb2020` / `bb2025`.

Owner side is derived from the player owning the skill (attacker or defender), replacing `appliesToDefender`.

### 3.3 Collecting candidates

* `SkillBehaviour` keeps a list of registered modifications (`registerModifier` adds instead of replacing).
* `Player.getUnusedInjuryModifications(InjuryType)` returns all unused ones.
* New service class (instance methods, see `AGENTS.md`) `server/injury/modification/bb2025/chain/InjuryModificationCandidateService`:
  `List<InjuryModificationCandidate> candidates(GameState, InjuryResolutionState)`:
  1. Determine the coach to ask (rule 3). None → empty list.
  2. Collect unused modifications of attacker and defender that belong to that coach, plus the optional regular
     skill modifications whose game option is enabled (3.11), excluding those already in the applied list.
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

* A decline ends the participation of that coach in the phase (`InjuryResolutionState.decline`, reset on phase
  change): the state is unchanged, so the same candidates would be offered again. The other coach can still be
  asked in the Crushing Blow case of rule 3; otherwise the phase is finished.
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

### 3.11 Optional regular skills (Mighty Blow, Claws, Dirty Player, A Sneaky Pair, Lethal Flight, Arm Bar)

#### Game options

New `GameOptionId`s (`GameOptionId`, `GameOptionIdFactory`, `GameOptionFactory` with default `false` and
messages, e.g. "Mighty Blow is optional and chosen by the coach after the roll"):

| Option | Skill (identified by property) | Phases | Injury types |
|---|---|---|---|
| `MIGHTY_BLOW_OPTIONAL("mightyBlowOptional")` | `affectsEitherArmourOrInjuryOnBlock` | `ARMOUR`, `INJURY` (exclusive) | Block, Bomb (`BOMB_USES_MB`) |
| `CLAWS_OPTIONAL("clawsOptional")` | `reducesArmourToFixedValue` | `ARMOUR` | Block |
| `DIRTY_PLAYER_OPTIONAL("dirtyPlayerOptional")` | `affectsEitherArmourOrInjuryOnFoul` | `ARMOUR`, `INJURY` (exclusive) | Foul, FoulForSpp |
| `SNEAKY_PAIR_OPTIONAL("sneakyPairOptional")` | `affectsEitherArmourOrInjuryWithPartner` | `ARMOUR`, `INJURY` (exclusive) | Foul, FoulForSpp, Stab, StabForSpp |
| `LETHAL_FLIGHT_OPTIONAL("lethalFlightOptional")` | `affectsEitherArmourOrInjuryOnTtm` | `ARMOUR`, `INJURY` (exclusive) | TTMHitPlayerForSpp |
| `ARM_BAR_OPTIONAL("armBarOptional")` | `affectsEitherArmourOrInjuryOnDodge` / `...OnJump` | `ARMOUR`, `INJURY` (exclusive) | DropDodge, DropDodgeForSpp, DropJump |

Defaults keep today's behaviour, so existing leagues are unaffected. Options are only evaluated by BB2025 code.

#### Single source of truth

A service `server/injury/modification/bb2025/OptionalSkillModifierService` (instance methods) answers
`boolean isOptional(Game, ArmorModifier|InjuryModifier)`: the modifier is registered to a skill with one of the
properties above and the matching option is enabled. It is used by both

* the **base evaluation** (BB2025 phased injury types, 3.5) to **exclude** these modifiers, and
* the **candidate service** (3.3), which only creates the corresponding modifications when the option is
  enabled,

so a modifier is never applied twice or lost. When the option is disabled the base evaluation stays identical to
today (incl. `handleChainsawAndMb` and the `CLAW_DOES_NOT_STACK` fallback).

#### Modification classes

`MightyBlowModification`, `ClawsModification`, `DirtyPlayerModification`, `SneakyPairModification`,
`LethalFlightModification`, `ArmBarModification` in `modification/bb2025`
(`ChainableInjuryModification`, `effect = MODIFIER`, usage `REGULAR`):

* `isApplicable` reuses the skill's registered modifier: build an `ArmorModifierContext` /
  `InjuryModifierContext` for the current state and call `appliesToContext`, so Stab, Chainsaw, foul, vomit,
  distraction, AV, TTM, partner marking and team checks are not duplicated. `ignoresArmourModifiersFromSkills`
  and cancelling skills (Iron Hard Skin vs. Claws, logic of `ArmorModifierFactory.findArmorModifiers`) are
  respected. Exception Arm Bar: its modifiers never apply via `appliesToContext`, applicability is the existing
  Arm Bar player lookup (see below).
* The `InjuryTypeBlock.Mode` of the injury type is respected: `DO_NOT_USE_MODIFIERS` → never applicable,
  `USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES` → only Claws / Mighty Blow in the armour phase, `REGULAR` →
  not against team mates. The mode therefore has to be accessible from the resolution state.
* "Armour or injury": not applicable in the `INJURY` phase if the same skill is in the applied list for the
  `ARMOUR` phase. The existing `appliesToContext` check on armour modifiers keeps working because accepted
  armour modifiers are part of the recomputed context.
* "Changes the result": armour phase → armour not broken now and broken after applying; injury phase → the
  interpreted injury (stunned / KO / casualty) differs after applying.
* Restriction for ordering: Claws `STAT` (AV 9+), all others `NONE`.

#### Owner and asked coach

| Skill | Owner (counts as attacker for rule 3) |
|---|---|
| Mighty Blow, Claws | blocker (bomber for bombs) |
| Dirty Player, A Sneaky Pair | fouler / stabber; A Sneaky Pair is never applicable without an attacker (e.g. `InjuryTypeStab` from `StepCatchScatterThrowIn`) |
| Lethal Flight | thrown player (`InjuryTypeTTMHitPlayerForSpp`; `InjuryTypeTTMHitPlayer` has no attacker and no Lethal Flight) |
| Arm Bar | the Arm Bar player, i.e. an opponent of the falling player |

Arm Bar is the only case where the injury otherwise has no attacker (`InjuryTypeDropDodge`, `InjuryTypeDropJump`).
The candidate service therefore asks the injury type for the **potential attackers** of the state instead of only
`attackerId`: for the drop injury types these are the eligible Arm Bar players. With `ARM_BAR_OPTIONAL` enabled:

* the eligible Arm Bar player lookup (`avOrInjModifierSkill` in the drop injury types, `armBarPlayers` in
  `StepMoveDodge`) is moved into one service (e.g. `ArmBarPlayerService`) used by the injury types, the step and
  `ArmBarModification`;
* `StepMoveDodge` no longer shows the `ARM_BAR` player choice before the injury. It creates the drop injury type
  with all eligible Arm Bar players;
* Arm Bar is offered **once** (not per player) to the coach of the Arm Bar players;
* on acceptance with several eligible players the `ARM_BAR` player choice (`DialogPlayerChoiceParameter`) is
  shown as a sub decision, like the `PRO` step for Old Pro; the chosen player becomes the attacker (SPP for
  casualties, `injuryContext.setAttackerId`). With one eligible player it is chosen without dialog;
* on decline in both phases no Arm Bar player becomes the attacker, as today without Arm Bar.

With the option disabled `StepMoveDodge` keeps choosing the player up front exactly as today.

#### Stacking rules in the recomputation

Accepted optional modifiers are kept with the other skill modifiers and re-added after the base evaluation
(3.1). The armour check must then reproduce today's rules, preferably in one place (e.g. a BB2025
`ArmourBreakEvaluationService`) instead of per injury type:

* Claws with `CLAW_DOES_NOT_STACK`: broken if the unmodified roll is 8+ **or** the roll with all other modifiers
  breaks; Claws only "changes the result" if the unmodified roll is 8+.
* Chainsaw without `MB_STACKS_AGAINST_CHAINSAW`: Mighty Blow is not applicable (today the chainsaw branch never
  adds skill modifiers); with the option Mighty Blow is only applicable if Chainsaw alone does not break.
* Bombs: Mighty Blow is only applicable with `BOMB_USES_MB`, attacker is the bomber.

#### Interaction with the chain

* Optional skills are ordinary candidates: listed together with e.g. Crushing Blow and sorted by rule 4
  (`REGULAR` first, Claws before Mighty Blow). Choosing "none" ends the phase, as for every other decision.
* Declining an "armour or injury" skill (Mighty Blow, Dirty Player, A Sneaky Pair, Lethal Flight, Arm Bar) in the
  armour phase does not consume it: if the armour gets broken later in the same phase by another skill, or the
  armour phase ends with broken armour, it is offered in the injury phase.
* Mighty Blow accepted on the armour, then the armour is re-rolled (Dwarfen Grit): the modifier stays (3.1) and
  Mighty Blow is still not available for the injury roll.
* `CrushingBlowModification` (bb2025): the "move Mighty Blow from armour to injury" special case is only needed
  while `MIGHTY_BLOW_OPTIONAL` is disabled. With the option enabled the coach simply chooses Crushing Blow
  instead of Mighty Blow, the special case must not trigger.
* Dirty Player and A Sneaky Pair on a foul: independent skills, each exclusive between armour and injury on its
  own, so both may be applied to the armour, both to the injury or one to each. With only one of the two options
  enabled, the other skill stays in the base evaluation (Sneaky Pair still only added there if the armour holds
  with the other base modifiers) and the optional one is offered on that result.
* Lethal Flight: offered to the coach of the thrown player; a thrown player landing on a team mate never gets it
  (team check of the modifier).
* Arm Bar with Diving Tackle: the Diving Tackle player is one of the eligible Arm Bar players (existing lookup).
* Bombs (`InjuryTypeBombWithModifier*`): Mighty Blow follows the same switch when `BOMB_USES_MB` is enabled.

#### Reporting and client

* An accepted optional skill produces a `ReportSkillUse` (Claws: the existing "uses Claws to reduce opponent's
  armour to 8+" line) followed by the new armour / injury result.
* `InjuryMessage` (bb2025) must not print the Claws line or the modifier twice when Claws arrives via a
  modification instead of the base armour modifiers.
* The asked coach sees the same single / selection dialog as for other candidates (3.7), no new client dialog.

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
8. **Optional regular skills** (3.11): game options, `OptionalSkillModifierService`, exclusion in the BB2025 base
   evaluation, `MightyBlowModification` / `ClawsModification` / `DirtyPlayerModification` /
   `SneakyPairModification` / `LethalFlightModification` / `ArmBarModification`, central armour break
   evaluation (Claws / Chainsaw stacking), `ArmBarPlayerService` and the deferred `ARM_BAR` player choice in
   `StepMoveDodge` / the decision loop, `CrushingBlowModification` special case gated by the option, client
   log check. Depends on steps 2–5; with all options disabled no behaviour change.
9. **Change list** entry in the latest `VersionChangeList` (`ChangeList.java`), without naming the rules
   version, including a feature entry for the new optional skill game options.

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
* Optional skills, option **disabled**: all six skills applied automatically, no dialog, results identical to
  today (incl. `CLAW_DOES_NOT_STACK`, Chainsaw, team mate modes, up front `ARM_BAR` player choice).
* `MIGHTY_BLOW_OPTIONAL`: armour holds, +1 breaks → offered; declined → armour holds, no injury roll. Armour
  breaks without it → offered in the injury phase, only if it changes the injury result. Accepted on armour →
  not offered on injury.
* `MIGHTY_BLOW_OPTIONAL` with Crushing Blow: both offered, Mighty Blow first; Crushing Blow chosen → armour
  broken → Mighty Blow offered on injury; the old "move Mighty Blow" special case does not trigger.
* `MIGHTY_BLOW_OPTIONAL` + Dwarfen Grit re-roll after Mighty Blow broke the armour: modifier kept on re-roll.
* `CLAWS_OPTIONAL`: offered only for AV 9+ and a roll of 8+ that does not break otherwise; not offered against
  Iron Hard Skin; with `CLAW_DOES_NOT_STACK` an accepted Mighty Blow does not combine with Claws.
* `DIRTY_PLAYER_OPTIONAL`: offered on fouls only, armour or injury exclusively; not offered on blocks.
* `SNEAKY_PAIR_OPTIONAL`: offered on foul and Stab only while the partner marks the defender; together with
  `DIRTY_PLAYER_OPTIONAL` both can be applied to the armour; with only one option enabled the other skill is
  applied automatically as today; Stab from a scattered ball (no attacker) never offers it.
* `LETHAL_FLIGHT_OPTIONAL`: thrown player lands on an opponent → offered to the thrown player's coach, armour or
  injury exclusively; landing on a team mate → not offered.
* `ARM_BAR_OPTIONAL`, failed dodge with one Arm Bar player: offered to the opponent's coach, no up front player
  choice; accepted → Arm Bar player is attacker and gets the casualty SPP; declined → no attacker set.
* `ARM_BAR_OPTIONAL`, several Arm Bar players: no up front choice; accepted → `ARM_BAR` player choice, chosen
  player is attacker; only answers of the asked coach with an eligible player are accepted.
* `ARM_BAR_OPTIONAL`, failed jump with Diving Tackle: the tackler's Arm Bar is offered.
* Team mate block (`USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES`): Claws / Mighty Blow offered for armour only.

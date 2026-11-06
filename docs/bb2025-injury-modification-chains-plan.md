# BB2025: Chained Armour and Injury Modifications

Status: **in progress**, steps 1, 2 and 4 implemented in #351 (not used by the game flow yet). Added to the plan
afterwards: combinations of modifier skills (rule 6, 3.5), the tree of evaluated injury contexts replacing the
decision loop on a single state (3.1, 3.7, 3.10) and the build / offer eligibility modes (rule 7, 3.1.3). #351
has to be adapted: the per skill result check of the `MODIFIER` chain modifications moves to the combination
service, `isApplicable` gets the eligibility mode, `InjuryResolutionState` becomes the node (see steps 3 and 4).
Scope: **BB2025 only**. `bb2016` and `bb2020` keep the current "one precomputed alternative" behaviour untouched.

---

## 1. Goal

Skills that modify an armour or injury roll after it was made (Crushing Blow, Dwarfen Grit, Krump and Smash,
Old Pro, Savage Mauling, Slayer, Toxin Connoisseur, ...) are currently limited to a single modification per
injury. The goal is to allow arbitrarily long chains of such modifications, with new options appearing whenever
a previous decision changed the result. All reachable outcomes are precomputed as a tree of evaluated injury
contexts (generalising today's normal / alternate pair), the decisions of the coaches walk that tree.

Additionally, regular modifier skills that are applied automatically today (Mighty Blow, Claws, Dirty Player,
A Sneaky Pair, Lethal Flight, Arm Bar)
can be made optional per game option. When their option is enabled they are no longer part of the base
evaluation but take part in the decision chain like any other modification (see 3.12).

### 1.1 Rules (confirmed)

1. **Chains of any length per side.** After every accepted modification the result is re-evaluated and the set
   of usable skills is determined anew. A modification can make other skills usable, e.g. Crushing Blow breaks
   the armour, afterwards Dwarfen Grit becomes usable for the defender. Each possible result is precomputed as a
   node of a tree, accepting an option moves to the corresponding child node (3.1, 3.7).
2. **Each roll is re-rolled at most once.** Once the armour roll was re-rolled (by any skill of any side), no
   further armour re-roll is offered. The same applies separately to the injury roll. Example: Krump and Smash
   re-rolled the armour and broke it, Dwarfen Grit is no longer available.
   Modifier skills (adding armour or injury modifiers) are not limited by this rule.
3. **Exactly one coach is asked per state.**
   * Armour phase, armour **not** broken: coach of the attacker (active or passive, e.g. Skull).
     Without an attacker nobody is asked. For injuries without an attacker that are caused by a player's skill
     (Arm Bar on a failed dodge / jump) that player counts as the attacker, see 3.12.
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

   Options combining several skills (rule 6) are listed after single skill options, fewer skills first; options
   of the same size are compared skill by skill with this ordering.
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
   * They are only offered when applying them, alone or combined with other modifier skills (rule 6), changes
     the current result (armour broken / injury result), i.e. they are never offered just to be shown in the log.
   * Being `REGULAR` skills they are not marked as used; "already used for this injury" is derived from the
     list of applied modifications.
6. **Combinations of modifier skills.** Like the BB2025 agility modifier selection (e.g. several optional dodge
   modifiers rescuing a failed dodge), modifier skills are offered as combinations: every **minimal** combination of
   the asked coach's `MODIFIER` skills that changes the result is one option. A skill that only helps together with
   others is offered only as part of such a combination, never alone.
   Example: AV 9+, armour roll 7. Crushing Blow alone (8) and Mighty Blow alone (8, `MIGHTY_BLOW_OPTIONAL`) do not
   break the armour, both together (9) do, so the option "Mighty Blow + Crushing Blow" is offered.
   * The coach picks at most one option per decision. All skills of the chosen option are applied together, so no
     skill is spent on a combination the coach does not complete.
   * Re-roll skills are never combined, each is a single option (the result of a re-roll is unknown).
   * Afterwards the state is re-evaluated as after any other decision (rule 1).
7. **Eligibility at build and at offer time.** When the available modifications are calculated (building the
   tree), the affected players are treated as standing, i.e. their player state is ignored. Before an option is
   actually offered, the eligibility of each of its skills is evaluated again taking all constraints into account
   (skill used meanwhile, player state, Pro availability, ...). If any skill of an option is no longer available
   the option is not offered. See 3.1.3.

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
modifications exclude each other, the armour branch discards injury modifications, reporting and turnover logic
assume one modification level, eligibility is decided once when the injury is created although the player state
can change before the decision (e.g. Steady Footing in a Multiple Block).
Kept on purpose: dice of declined modifications are rolled up front (Old Pro today, every re-roll branch of the
tree), bounded by the dice pool of 3.1.4 and never revealed.

---

## 3. Design

### 3.1 Tree of modification aware injury contexts

The single precomputed alternative (`InjuryContext.modifiedInjuryContext`, today a two element "normal /
alternate" pair) is generalised to a **tree of fully evaluated injury contexts**. The tree is built completely
when the injury is created (3.1.2); the decisions afterwards only **walk** it (3.7).

#### 3.1.1 Node

A node (`InjuryNode`, BB2025, server side) holds:

* the `InjuryContext` of this outcome: dice, armour / injury modifiers, armour broken, injury, casualty, serious
  injury (+ decay), stun → KO, apothecary status, send to box data; evaluated exactly like a context today
  (base evaluation, applied modifications, `UtilServerInjury.evaluateInjuryContext`, 3.10);
* the `InjuryModificationOption` (3.5) leading from the parent to this node (`null` for the root) and the ordered
  list of all modifications applied on the path from the root (needed for the recomputation below, for "already
  used on this path" and for the "armour or injury" exclusivity of 3.12);
* `armourReRolled` / `injuryReRolled` (rule 2) and the phase, `ARMOUR` or `INJURY`;
* `turnover`: whether this outcome ends the turn, computed by the caller supplied rule (3.10);
* the reports belonging to the edge from the parent (`ReportSkillUse`, `ReportOldPro`, ...), published only when
  the walk takes that edge (3.9);
* the children, grouped into **decision groups** in rule 3 order. A group is one asked coach in one phase with
  its ordered options, each option pointing to its child:

  | Node | Groups (in order) |
  |---|---|
  | `ARMOUR`, armour holds | attacker (armour) |
  | `ARMOUR`, armour broken | defender (armour), attacker `offeredOnBrokenArmour` (armour), injury group |
  | `INJURY` | injury group |

  The injury group is asked to the attacker's coach, without attacker to the defender's coach (rule 3).
  Children reached via an armour option are `ARMOUR` nodes, children reached via an injury option are `INJURY`
  nodes, so armour options are never offered again once an injury option was accepted.

The result of a node is always **recomputed** from the node's dice plus the modifications applied on its path,
never by modifying a copy of the parent in place:

1. Base evaluation of the injury type (`armourRoll(..., roll = false)` / injury interpretation) on the node's
   dice.
2. Applying all modifications of the path in order.

**Pitfall:** `InjuryTypeBlock.armourRoll` (and other injury types, 5 occurrences in `injuryType/`) call
`clearArmorModifiers()`. The modifiers added by applied modifications must therefore be kept separate from the
base modifiers and be re-added after the base evaluation, otherwise e.g. Crushing Blow would be lost when Dwarfen
Grit re-rolls the armour.

Storage: nodes are kept in a flat list with ids (children reference ids), which keeps the JSON flat (3.11).
`InjuryResult` keeps the root and the id of the **current** node instead of `swapToAlternateContext`.

Nodes are **not shared** between paths, every node has exactly one parent. The options of a node depend on its
path: in which phase each skill was applied (Mighty Blow on the armour is no longer available for the injury, on
the injury it is), the order of application (stacking rules) and the edge reports. A key covering all of that would
rarely match, as rule 3 fixes the order of most decisions, so the gain does not justify the risk.

#### 3.1.2 Building the tree

```
build(node):                                   // node is already evaluated
  for group in decisionGroups(node):           // rule 3
    candidates = candidateService.candidates(game, node, group, BUILD)    // 3.1.3
    for option in combinationService.options(game, node, candidates):  // 3.5
      child = nodeFactory.create(node, option)   // copy, apply, recompute, evaluate, turnover
      node.addChild(group, option, child)
      build(child)
```

* Matches the intended flow: 1. find all applicable modification combinations, 2. create and evaluate a context
  per combination and store it on the parent, 3. repeat for every new node.
* Terminates: every edge applies at least one skill not used on the path, re-rolls are limited by rule 2.
* Size: bounded by the few chain skills of the two players and the cap of 5 combined candidates (3.5). As a safety
  net the build stops expanding after a fixed number of nodes (e.g. 1000, logged as an error); an unexpanded node
  is expanded with the same `BUILD` rules when the walk reaches it.
* `build` has no game side effects apart from consuming dice from the injury's dice pool (3.1.4). Skills are only
  marked used, Pro is only rolled and reports are only published by the walk (3.7).

#### 3.1.3 Eligibility: assumed standing at build, re-validated at offer

The tree is built at injury creation, but the options are offered later, possibly after other steps changed the
game. Example (Multiple Block, Both Down): both injuries are created in `DROP_FALLING_PLAYERS`, the attacker's
`STEADY_FOOTING` runs afterwards and before the defender's injury is resolved. A Pro re-roll used there makes the
attacker's Old Pro unavailable; passing Steady Footing turns the attacker from `FALLING` into `MOVING`, which
gives back the tackle zones Crushing Blow, Ram, Slayer and Dwarven Scourge require.

Therefore eligibility is checked twice, by the same check with two modes (`EligibilityMode`):

* **`BUILD`** (tree construction): every condition derived from the `PlayerState` of an affected player
  (attacker, defender, skill owners, the A Sneaky Pair partner, Arm Bar players) is evaluated as if that player
  were standing: base state `STANDING`, active, tackle zones, not confused, hypnotised or distracted, Pro not used
  this turn (`hasUsedPro`), not prone / stunned for `eligibleForPro`. All other conditions are checked normally:
  injury type, teams, phase, skill used markers, skills used on the path, rule 2, acting player and turn mode for
  Pro. Usage only changes towards "not available" until the injury is resolved, so checking it at build time
  cannot lose an option, while the player state can change in both directions (`FALLING` → `MOVING`). The tree is
  thus a **superset** of what can be offered.
  Positions (adjacency, the partner marking the defender apart from his tackle zones, Arm Bar squares) are checked
  as they are at build time: in the current flows nobody is moved between creating and resolving an injury
  (pushes and follow ups happen before). Should a flow move players in between, positions have to be relaxed at
  build time as well.
* **`OFFER`** (before a group is shown, 3.7): every skill of every option of the group is checked again with all
  constraints on the real game: unused, real player state (`PlayerState.hasTacklezones`, distraction, ...), Pro
  availability (`UtilServerReRoll.isProReRollAvailable`), positions. An option with **any** ineligible skill is
  removed, a combination is never shrunk to its remaining skills. This loses nothing: a subset that reaches the
  goal on its own is an option of its own (3.5, minimality only drops supersets).

The evaluation of a node (armour broken, injury, ...) is not affected by the mode, it always uses the real game,
e.g. a non optional Mighty Blow in the base evaluation still depends on the real distraction state.

Should a future chain modification require a player state other than standing (none of the current ones does),
`BUILD` must keep that condition instead of assuming standing.

#### 3.1.4 Dice

Building the tree rolls the dice of branches that are never taken, e.g. the Dwarfen Grit re-roll is rolled
although the defender may decline it. To keep this bounded and deterministic, each injury owns a dice pool
(`InjuryDicePool`, part of the root) whose entries are rolled lazily on first use and shared by all nodes:
armour roll, armour re-roll, injury roll, injury re-roll, casualty roll and its decay roll.
Old Pro takes its single die from the armour re-roll entry (first die). This is valid because only one branch is
ever realised and rule 2 allows each roll to be re-rolled once per path. It limits the dice per injury to a known
maximum and makes the order of `TestRolls` predictable (depth first build order). *Assumption of this plan, not
part of the confirmed rules.*

**Hidden dice must never influence what is offered.** Options are derived only from the visible state of the
current node (3.5 goals are deterministic modifier simulations on visible dice, re-roll options use "can it help
its owner" without the re-rolled value). A re-roll option is never removed or reordered because of its child's
result, the dialog shows "re-roll armour" / "re-roll injury" instead of a result (3.8), an armour option shows
"armour broken" but never the injury of its child, and the tree is never sent to the client (3.11).

### 3.2 Self-describing modifications

`InjuryContextModification` is duplicated into a BB2025 variant (no inheritance between ruleset classes, see
`AGENTS.md`). Implemented in `server/injury/modification/bb2025/chain`: `ChainableInjuryModification` plus one
`*ChainModification` class per skill (suffix avoids clashes with the legacy BB2025 classes), exposing:

| Method | Purpose |
|---|---|
| `Set<Phase> phases()` | `ARMOUR`, `INJURY` or both (Ram / Dwarven Scourge / Slayer). |
| `Effect effect()` | `MODIFIER` or `RE_ROLL`. Re-roll: Krump and Smash, Lone Fouler, Master Assassin, Dwarfen Grit, Old Pro (single armour die), Savage Mauling (injury). Modifier: Crushing Blow, Toxin Connoisseur, Ram / Dwarven Scourge, Slayer. |
| `Restriction restriction()` | `STAT`, `KEYWORD`, `NONE`. Ideally declared as a restriction object (e.g. `KeywordRestriction(BIG_GUY)`) that is used both for the applicability check and for ranking, so both can not diverge. |
| `boolean isApplicable(game, node, phase, EligibilityMode)` | Side effect free check, derived from today's `try*`, `armourModificationCantHelp`, team checks and `ignoresArmourModifiersFromSkills`. `RE_ROLL`: includes "can it help its owner on the current node", judged on visible dice only (3.1.4). `MODIFIER`: only the preconditions (phase, injury type, teams, tackle zones, armour still holding / no casualty yet, skill specific conditions), **not** whether the skill alone changes the result: that is decided per combination by `InjuryModificationCombinationService` (3.5), as a modifier may only help together with others. #351 still checks the result per skill (`breaksArmourWith` / `changesInjuryWith` in `isApplicableToArmour` / `isApplicableToInjury`), this check moves to the combination service. `EligibilityMode` (3.1.3): `BUILD` evaluates player state conditions as standing, `OFFER` checks everything on the real game incl. unused / Pro availability. |
| `void apply(node)` | Changes dice or adds modifiers on the child under construction; re-rolls take their dice from the injury's dice pool (3.1.4). `MODIFIER`: deterministic (no dice), so the combination service can apply it to a copy to simulate a combination. Only called while building the tree, never by the walk. |
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
  `List<InjuryModificationCandidate> candidates(GameState, InjuryNode, DecisionGroup, EligibilityMode)`, called
  with `BUILD` while building the tree (3.1.2); the walk uses the `OFFER` check per skill of the stored options
  instead (3.1.3):
  1. The group determines the coach (rule 3). No coach → empty list.
  2. Collect unused modifications of attacker and defender that belong to that coach, plus the optional regular
     skill modifications whose game option is enabled (3.12), excluding those applied on the node's path.
  3. Filter by phase, valid injury type, `isApplicable(..., BUILD)`, re-roll flags.
  4. Sort with `InjuryModificationOrderingService` (rule 4).
* The candidates are turned into the options offered to the coach by `InjuryModificationCombinationService` (3.5):
  re-roll candidates as single options, modifier candidates as minimal combinations.

### 3.4 Ordering

`InjuryModificationOrderingService` provides a `Comparator<Candidate>`: usage rank, restriction rank, skill name.

`AgilityModifierSelectionService.usageCost` is **not** reused as is: it ranks `REGULAR` and `ONCE_PER_TURN`
equally and `SPECIAL` like `ONCE_PER_GAME`, both differ from rule 4.

Options (3.5) are ordered by number of skills ascending (single options first, as in the agility modifier
selection), then skill by skill with the candidate comparator (the skills of each option are kept in rule 4 order),
then by label.

### 3.5 Combinations of modifier skills

Modelled on the BB2025 agility modifier selection (`server/util/bb2025/AgilityModifierSelectionService`, used by
`DodgeModifierSelectionService` / `step/bb2025/move/StepMoveDodge` and the other agility rolls): the coach is
offered the minimal combinations of optional modifier skills that rescue the roll, picks one in a single dialog,
and all skills of it are committed at once.

New service (instance methods) `server/injury/modification/bb2025/chain/InjuryModificationCombinationService`:
`List<InjuryModificationOption> options(GameState, InjuryNode, List<InjuryModificationCandidate>)`.

1. **Re-roll candidates** become one single option each, they are never combined. **Candidates offered on broken
   armour** (`offeredOnBrokenArmour`, Crushing Blow moving Mighty Blow) are single options as well.
2. **Modifier candidates**: all non empty subsets are enumerated by ascending size. Like
   `AgilityModifierSelectionService.MAX_OPTIONAL_MODIFIERS` the number of combined candidates is capped at 5
   (the first five in rule 4 order) to keep the 2^n enumeration safe.
3. Each subset is **simulated** on a copy of the node (`InjuryContextCopyService`, extended to copy the
   node incl. the skill modifiers of 3.1): its modifications are applied in rule 4 order and the result
   is recomputed with the central evaluation, so stacking rules (`CLAW_DOES_NOT_STACK`, Chainsaw / Mighty Blow,
   Crushing Blow, `ignoresArmourModifiersFromSkills`) apply to the combination as a whole. For Arm Bar the
   simulation does not need the `ARM_BAR` player choice, every eligible player adds the same modifier.
4. **Goal** depends on the phase and the owner side:
   * armour phase (armour holds, attacker side): the armour is broken;
   * injury phase: the interpreted injury is better for the owner than the current one (attacker side: stunned <
     KO < casualty).
   A subset reaching the goal is an option; its simulation is kept as the child node (3.1.2), it is not computed
   twice. The goals only use visible dice (3.1.4).
5. **Minimality** (like `findOptions` / `removeRedundantSupersets`): an option is dropped if one of its proper
   subsets reaches the same or a better result. The injury phase can therefore offer "Mighty Blow" (KO) and
   "Mighty Blow + Slayer" (casualty) side by side, but not "Mighty Blow + Slayer" if it only reaches KO as well.
   A combination whose additional skill has no effect (e.g. Claws + Mighty Blow with `CLAW_DOES_NOT_STACK`) is
   dropped by the same rule.
6. Unlike `removeRedundantSingleSkills`, single options reaching the same result are **not** dropped: the skills
   differ in what remains available afterwards (e.g. Crushing Blow on the armour keeps Mighty Blow for the injury
   roll, Mighty Blow on the armour consumes it). The ordering (3.4) expresses the preference instead.
7. Constraints within a combination:
   * every skill at most once; skills usable in both phases (Ram / Dwarven Scourge / Slayer, the "armour or
     injury" skills of 3.12) are applied to the current phase and are therefore no longer available for the injury
     roll;
   * only candidates of one owner side are combined. In a team mate block the coach owns both players, their
     skills are never mixed because "better" points in opposite directions;
   * the subsets are built from the `BUILD` candidates, the `OFFER` check later removes every option containing an
     ineligible skill (3.1.3).

`InjuryModificationOption` (ffb-common, JSON serialisable, similar to `ModifierChoiceOption`): the skills in rule 4
order, the label ("Mighty Blow + Crushing Blow") and a short description of the resulting armour / injury result,
or "re-roll armour" / "re-roll injury" for re-roll options. `ModifierChoiceOption` is not reused, its minimum roll
has no meaning here.

The subset enumeration mirrors `AgilityModifierSelectionService` but is not reused directly: its success
criterion is a minimum roll, and its single skill pruning and `usageCost` differ from rules 4 and 6.

Example (`MIGHTY_BLOW_OPTIONAL`, AV 9+, armour roll 7, attacker with Mighty Blow and Crushing Blow):

| Subset | Armour | Result |
|---|---|---|
| Mighty Blow | 8 | holds, no option |
| Crushing Blow | 8 | holds, no option |
| Mighty Blow + Crushing Blow | 9 | broken → option "Mighty Blow + Crushing Blow" |

The attacker's coach accepts it, Crushing Blow is marked used, Mighty Blow is consumed for the armour, the armour is
broken and the defender's coach is asked next (e.g. Dwarfen Grit). A re-roll keeps both modifiers (3.1).
Without `MIGHTY_BLOW_OPTIONAL` Mighty Blow is part of the base evaluation (roll 7 + 1 = 8), Crushing Blow alone
breaks the armour and is offered as a single option, as today.

### 3.6 Phased injury types

`ModificationAwareInjuryTypeServer.handleInjury` stays for `bb2016` / `bb2020`. For BB2025 the injury types
additionally expose the phases separately so that the node factory (3.1.2) can evaluate every node:

* `evaluateArmour(node)` – base evaluation on the node's dice without rolling,
* `evaluateInjury(node)` – base injury interpretation, only for nodes with broken armour.

All dice (armour, injury, casualty, re-rolls) come from the injury's dice pool (3.1.4). The injury roll is rolled
when the first node with broken armour is built, but reported only when the walk reaches the injury phase (3.9).
Which variant is used is selected through the BB2025 mechanic (e.g. `InjuryMechanic`), not through inheritance
between ruleset classes.

### 3.7 Walking the tree in `step/bb2025/shared/StepHandleDropPlayerContext`

```
current = root; declined = {}
loop:
  group = first group of current (rule 3 order) not in declined
          whose re-validated options (OFFER, 3.1.3) are not empty
  none → finish with current
  dialog for the group's coach (one single skill option → DialogSkillUse, otherwise selection dialog)
    accepted → sub decisions (PRO step, ARM_BAR player choice)
               success → mark all skills of the option used, publish the edge reports,
                         current = child, declined = {}, continue loop
               PRO failed → Pro used, stay on current, continue loop (Old Pro now fails the OFFER check)
    declined → declined += group, continue loop
finish: re-evaluate current (3.10), apply turnover of current, dropPlayer
```

* This is the intended flow: when the context is evaluated, the respective coach is offered the first level of the
  tree; no (remaining) options → continue with the current node; an option is selected → move to its child and
  offer again; declined → continue with the current node.
* Before every dialog the options of the group are re-validated (3.1.3, `OFFER`), so options of a stale tree are
  never shown. A group whose options were all removed is skipped like a declined group.
* A decline ends the participation of that coach for the current node: the node is unchanged, the same options
  would be offered again. The next group of the node may still be asked (rule 3 exception, injury group). Moving
  to a child resets the declined groups, as the new result may give new options to the same coach.
* Every accepted option moves one edge down, the walk terminates.
* An accepted option is applied atomically: its child already contains all modifications of the option. Skills are
  marked used only now. Combinations contain only `MODIFIER` skills, so the `PRO` step never interrupts a
  combination; the `ARM_BAR` player choice (3.12) is made before the edge is taken.
* Old Pro: the edge to its child is only taken after a successful `PRO` roll. The die of that child was taken from
  the dice pool at build time (3.1.4) but is only revealed (reported) when the edge is taken. On a failed `PRO`
  roll the walk stays on the current node; other options of the same group remain if they pass the `OFFER` check.

Example: armour holds → attacker accepts Crushing Blow → child: armour broken → defender accepts Dwarfen Grit
(re-roll, `armourReRolled`) → child: armour holds → attacker group: Krump and Smash is not a child (rule 2), only
modifier options → none → finish with that node.

### 3.8 Dialog

* Single option with a single skill: existing `DialogSkillUseParameter` (unchanged client behaviour).
* Otherwise a selection dialog listing the sorted, re-validated options (label and resulting result, e.g. "Mighty
  Blow + Crushing Blow: armour broken"; re-roll options only "re-roll armour" / "re-roll injury", 3.1.4) plus
  "none", modelled on the modifier combination list of `DialogReRollModifierChoiceParameter`
  / `DialogReRollModifierChoice` but without the re-roll part: a dedicated `DialogInjuryModificationChoiceParameter`
  with a `List<InjuryModificationOption>`. The client must keep the order sent by the server.
* The answer is a client command carrying the chosen skill list (like `ClientCommandReRollModifierChoice`).
  `handleCommand` only accepts it from the asked coach if the skill set equals one of the options of the group
  offered at the current node and every skill still passes the `OFFER` check (3.1.3; same hardening as
  `acceptModifierChoice` in the agility modifier selection). The options are stored in the tree, the step only
  keeps the current node id and the offered group.

### 3.9 Reporting

Reports are written incrementally instead of derived after the fact:

1. Armour roll of the root before the first decision.
2. Per edge taken by the walk: the reports stored on the child (`ReportSkillUse` per skill, skill specific reports
   such as `ReportOldPro`), then the child's armour / injury result once. Reports of edges not taken are never
   published.
3. Injury roll when the walk reaches the injury group of a node with broken armour or finishes on such a node,
   casualty parts when the walk finishes.

The step tells `StateMechanic.reportInjury` (bb2025) which parts to report; the skip derivation from
`InjuryModification` is no longer used for BB2025. Client log rendering has to be checked for repeated armour /
injury results within one injury.

### 3.10 Evaluation and turnover per node

* `UtilServerInjury.handleInjury` builds the tree for BB2025 instead of a single alternative: every node is
  evaluated when it is created with `evaluateInjuryContext` (serious injury and decay interpretation, stun → KO,
  apothecary status, send to box data, sound) and `handleIgnoringArmourBreaks`. No dice are rolled by this
  evaluation, the casualty dice come from the pool (3.1.4).
* Parts of the evaluation read game state that may change before the walk ends (apothecary availability, like
  today's up front evaluation of the alternative). The node the walk finishes with is therefore evaluated once more
  (no dice, no side effects) before `dropPlayer`.
* **Turnover per node.** `modifiedInjuryEndsTurn` (`DropPlayerContext`, `DropPlayerContextBuilder`, precomputed
  in `step/bb2025/block/StepBlockChainsaw`) is replaced by a turnover rule supplied by the producer of the
  `DropPlayerContext` and applied to every node. It must be serialisable (step JSON), so it is an id resolved to a
  service (e.g. `InjuryTurnoverRule`: `ALWAYS`, `NEVER`, `CHAINSAW_KICKBACK`, ...) rather than a lambda.
  Examples:
  * Chainsaw kickback with `CHAINSAW_TURNOVER_KICKBACK`: turnover only while the armour is broken, so the Old Pro
    child of a self inflicted Chainsaw injury that no longer breaks the armour has `turnover = false`;
  * plain falls (failed dodge / GFI): always a turnover, independent of the node.
* **Fouls.** Ejection stays in `StepReferee`, which runs after `HANDLE_DROP_PLAYER_CONTEXT` (`generator/bb2025/Foul`)
  and reads the selected node's dice through `INJURY_RESULT`, so a re-rolled armour or injury roll is judged on its
  final dice. Nodes additionally carry a "spotted" flag computed with the referee's rule (extracted into a service
  used by `StepReferee` and the node factory, so both cannot diverge); it is informational (e.g. for Old Pro on a
  spotted foul) and never decides the turnover itself.
* All BB2025 producers of a `DropPlayerContext` have to be checked for logic reading the injury result before the
  decisions and must supply their turnover rule: `StepBlockChainsaw`, `StepBreatheFire`, `StepSteadyFooting`,
  `StepDropFallingPlayers`, `StepThrowARock`, `StallingExtension`, `StepRightStuff`, `StepSpecialEffect`,
  `StepInitScatterPlayer`, `AnimalSavageryBehaviour`, plus BB2025 steps calling `UtilServerInjury.handleInjury`
  directly.

### 3.11 Serialisation

* The tree (flat node list, current node id, dice pool, turnover rule id) is part of the injury result JSON in the
  step state. It stays server side: it contains the dice of branches not taken, so it must not be part of any
  model or report sent to the clients.
* `initFrom` must still read the old `alternateContext` format for games saved mid step; it is converted to a root
  with a single child (the old alternative) and the old single decision behaviour.

### 3.12 Optional regular skills (Mighty Blow, Claws, Dirty Player, A Sneaky Pair, Lethal Flight, Arm Bar)

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

* the **base evaluation** (BB2025 phased injury types, 3.6) to **exclude** these modifiers, and
* the **candidate service** (3.3), which only creates the corresponding modifications when the option is
  enabled,

so a modifier is never applied twice or lost. When the option is disabled the base evaluation stays identical to
today (incl. `handleChainsawAndMb` and the `CLAW_DOES_NOT_STACK` fallback).

#### Modification classes

`MightyBlowModification`, `ClawsModification`, `DirtyPlayerModification`, `SneakyPairModification`,
`LethalFlightModification`, `ArmBarModification` in `modification/bb2025`
(`ChainableInjuryModification`, `effect = MODIFIER`, usage `REGULAR`):

* `isApplicable` reuses the skill's registered modifier: build an `ArmorModifierContext` /
  `InjuryModifierContext` for the node and call `appliesToContext`, so Stab, Chainsaw, foul, vomit,
  distraction, AV, TTM, partner marking and team checks are not duplicated. `ignoresArmourModifiersFromSkills`
  and cancelling skills (Iron Hard Skin vs. Claws, logic of `ArmorModifierFactory.findArmorModifiers`) are
  respected. Exception Arm Bar: its modifiers never apply via `appliesToContext`, applicability is the existing
  Arm Bar player lookup (see below).
* The `InjuryTypeBlock.Mode` of the injury type is respected: `DO_NOT_USE_MODIFIERS` → never applicable,
  `USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES` → only Claws / Mighty Blow in the armour phase, `REGULAR` →
  not against team mates. The mode therefore has to be accessible from the node.
* "Armour or injury": not applicable in the `INJURY` phase if the same skill was applied on the node's path in the
  `ARMOUR` phase. The existing `appliesToContext` check on armour modifiers keeps working because accepted
  armour modifiers are part of the recomputed context.
* "Changes the result" is not checked per skill but per combination by the combination service (3.5): armour
  phase → armour not broken now and broken after applying the combination; injury phase → the interpreted injury
  (stunned / KO / casualty) is better for the owner after applying it. A single optional skill is the smallest
  combination.
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
* Arm Bar passes the `OFFER` check (3.1.3) if at least one Arm Bar player is still eligible (e.g. still has tackle
  zones); the player choice below only lists those players;
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

* Optional skills are ordinary candidates: listed together with e.g. Crushing Blow, combined with the other
  modifier skills of the same coach (rule 6, 3.5) and sorted by 3.4 (`REGULAR` first, Claws before Mighty Blow).
  Choosing "none" ends the phase, as for every other decision.
  Example: AV 9+, roll 7, Crushing Blow and `MIGHTY_BLOW_OPTIONAL` → only "Mighty Blow + Crushing Blow" is offered;
  roll 8 → "Mighty Blow" and "Crushing Blow" are offered as single options, the combination is not (not minimal).
  Dirty Player and A Sneaky Pair with both options enabled are combined the same way.
* Declining an "armour or injury" skill (Mighty Blow, Dirty Player, A Sneaky Pair, Lethal Flight, Arm Bar) in the
  armour phase, alone or as part of a combination, does not consume it: if the armour gets broken later in the same
  phase by another skill, or the armour phase ends with broken armour, it is offered in the injury phase.
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
* The asked coach sees the same single / selection dialog as for other options (3.8), no additional client
  dialog.

---

## 4. Implementation steps

1. **Multiple registrations, no behaviour change.** List in `SkillBehaviour`, `getUnusedInjuryModifications`,
   existing code keeps using the first entry.
2. **BB2025 modification variant.** Duplicate into `modification/bb2025`, split check and apply, add `phases`,
   `effect`, `restriction`. Move all BB2025 behaviours to the new classes. Unit tests per modification for
   `isApplicable` / `apply`.
3. **Node tree and recomputation.** `InjuryNode` (evolving #351's `InjuryResolutionState`), decision groups,
   flat storage with ids, `InjuryDicePool`, node factory (recompute, `evaluateInjuryContext`, turnover rule,
   edge reports), tree builder with node cap, separation of base and skill modifiers, phased evaluation in the
   injury types, JSON incl. backwards compatibility. Unit tests: tree shape, node cap, dice pool order.
4. **Candidate and ordering services** with unit tests (coach selection, re-roll limit, ordering).
   `EligibilityMode` (3.1.3) for `isApplicable` of all chain modifications incl. Old Pro's Pro check (#351 uses
   `isProReRollAvailable` and `actingPlayerHasTacklezones` directly), unit tests for both modes.
   Follow-up to #351: remove the per skill result check from the `MODIFIER` chain modifications
   (`AvOrInjChainModification`, `CrushingBlowChainModification`, `ToxinConnoisseurChainModification`), add
   `InjuryModificationCombinationService` and `InjuryModificationOption` (3.5) with unit tests (minimal combinations,
   cap, re-rolls never combined, stacking rules, option ordering).
5. **Tree walk** in the BB2025 `StepHandleDropPlayerContext` (3.7): `OFFER` re-validation per group, `PRO`
   integration, command hardening, option selection dialog and command with the chosen skill list (server and
   client).
6. **Reporting** in the BB2025 `StateMechanic` and client log.
7. **Callers** (3.10): build the tree in `UtilServerInjury.handleInjury` for BB2025, turnover rule per
   `DropPlayerContext` producer replacing `modifiedInjuryEndsTurn`, re-evaluation of the final node, referee
   "spotted" rule extracted from `StepReferee`.
8. **Optional regular skills** (3.12): game options, `OptionalSkillModifierService`, exclusion in the BB2025 base
   evaluation, `MightyBlowModification` / `ClawsModification` / `DirtyPlayerModification` /
   `SneakyPairModification` / `LethalFlightModification` / `ArmBarModification`, central armour break
   evaluation (Claws / Chainsaw stacking), `ArmBarPlayerService` and the deferred `ARM_BAR` player choice in
   `StepMoveDodge` / the tree walk, `CrushingBlowModification` special case gated by the option, client
   log check. Depends on steps 2–5; with all options disabled no behaviour change.
9. **Change list** entry in the latest `VersionChangeList` (`ChangeList.java`), without naming the rules
   version, including a feature entry for the new optional skill game options.

## 5. Tests (`ffb-statetest`)

* Crushing Blow breaks armour → Dwarfen Grit offered to the defender → re-roll keeps the Crushing Blow modifier.
* Krump and Smash re-roll breaks armour → Dwarfen Grit **not** offered.
* Dwarfen Grit re-roll → armour holds → attacker is offered only modifier skills, no re-roll.
* Decline ends the phase, the same coach is not asked again for the same node.
* Stale tree, Multiple Block Both Down: the attacker uses Pro to re-roll Steady Footing after both injuries were
  created → the defender's armour is broken → Old Pro of the attacker is in the tree but not offered.
* Stale tree, Multiple Block Both Down: the attacker is `FALLING` when the injuries are created and passes Steady
  Footing → Crushing Blow (needs tackle zones) is offered on the defender's injury.
* Stale tree: a combination containing a skill that was used meanwhile (or whose owner lost his tackle zones) is
  not offered, the single options of its other skills still are if they reach the goal on their own; a group whose
  options are all removed is skipped.
* Hidden dice: a re-roll option is offered although its pre-rolled result is worse; the dialog shows no result
  for it; its die is not reported when declined.
* Dice pool: armour re-roll, injury roll and casualty roll are each consumed from `TestRolls` at most once per
  injury, in depth first build order, regardless of the number of nodes.
* Turnover per node: self inflicted Chainsaw with `CHAINSAW_TURNOVER_KICKBACK`, Old Pro un-breaks the armour →
  no turnover; foul with Lone Fouler re-rolling doubles on the armour → `StepReferee` judges the re-rolled dice.
* The apothecary status of the final node is re-evaluated when the apothecary was used in between (Multiple
  Block, both injuries casualties).
* Ordering: once per turn before once per half; keyword restricted (Slayer) before unrestricted at the same usage
  rank.
* Combinations: `MIGHTY_BLOW_OPTIONAL`, AV 9+, roll 7, Mighty Blow + Slayer against a Big Guy → only "Mighty Blow +
  Slayer" offered; accepted → Slayer used, Mighty Blow consumed for the armour, armour broken, defender asked next;
  Dwarfen Grit re-roll keeps both modifiers.
* Combinations: a superset reaching the same result as one of its subsets is not offered; in the injury phase
  "Mighty Blow" (KO) and "Mighty Blow + Slayer" (casualty) are offered side by side; single options before
  combinations.
* Combinations: re-roll skills are never part of a combination; declining a combination ends the phase without
  consuming any of its skills.
* Command with a skill set that is not one of the offered options (subset, superset, other coach) is rejected.
* Ram / Dwarven Scourge: armour modifier declined, injury modifier offered in the injury phase.
* Old Pro: failed `PRO` roll → walk stays on the node, the Old Pro die is not reported, other options still
  offered; self inflicted case (no attacker)
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
* `MIGHTY_BLOW_OPTIONAL` with Crushing Blow, armour needs +2: only "Mighty Blow + Crushing Blow" offered, neither
  skill alone; accepted → Mighty Blow is not offered on the injury roll.
* `MIGHTY_BLOW_OPTIONAL` + Dwarfen Grit re-roll after Mighty Blow broke the armour: modifier kept on re-roll.
* `CLAWS_OPTIONAL`: offered only for AV 9+ and a roll of 8+ that does not break otherwise; not offered against
  Iron Hard Skin; with `CLAW_DOES_NOT_STACK` an accepted Mighty Blow does not combine with Claws.
* `DIRTY_PLAYER_OPTIONAL`: offered on fouls only, armour or injury exclusively; not offered on blocks.
* `SNEAKY_PAIR_OPTIONAL`: offered on foul and Stab only while the partner marks the defender; together with
  `DIRTY_PLAYER_OPTIONAL` both can be applied to the armour, also as one combination when only both together break
  it; with only one option enabled the other skill is
  applied automatically as today; Stab from a scattered ball (no attacker) never offers it.
* `LETHAL_FLIGHT_OPTIONAL`: thrown player lands on an opponent → offered to the thrown player's coach, armour or
  injury exclusively; landing on a team mate → not offered.
* `ARM_BAR_OPTIONAL`, failed dodge with one Arm Bar player: offered to the opponent's coach, no up front player
  choice; accepted → Arm Bar player is attacker and gets the casualty SPP; declined → no attacker set.
* `ARM_BAR_OPTIONAL`, several Arm Bar players: no up front choice; accepted → `ARM_BAR` player choice, chosen
  player is attacker; only answers of the asked coach with an eligible player are accepted.
* `ARM_BAR_OPTIONAL`, failed jump with Diving Tackle: the tackler's Arm Bar is offered.
* Team mate block (`USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES`): Claws / Mighty Blow offered for armour only.

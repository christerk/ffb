# BB2025: Generic Optional Dodge Modifier Selection (Break Tackle + Consummate Professional)

Status: plan / not implemented
Scope: **BB2025 only**. `bb2016` and `bb2020` keep their current behaviour untouched.
Related follow-up (explicitly out of scope): `ffb-server/.../step/bb2025/move/StepJump.java`.

---

## 1. Goal

Replace the current hard-coded, Break-Tackle-specific dodge logic with a **generic, opt-in
"optional dodge modifier" mechanism** that can handle any number of skills contributing an
optional modifier to a dodge roll, and wire Consummate Professional into it.

Behavioural requirements:

| # | Requirement |
|---|---|
| R1 | Consummate Professional grants a once-per-game **+1** modifier to dodge rolls (BB2025). |
| R2 | A player may have both Consummate Professional and Break Tackle. |
| R3 | On a dodge roll, **neither** optional modifier is included by default. |
| R4 | On a **successful** dodge (without optional modifiers) keep the current behaviour. |
| R5 | On a **failed** dodge, or a dodge that **would fail because of Diving Tackle**: determine (a) whether a re-roll is available and (b) whether any subset of the available optional modifiers would guarantee success. |
| R6 | If either is true, prompt the coach with a dialog listing **all** viable options. |
| R7 | When several skills give the same modifier value and only a subset is needed, prefer the skills that can be used most often: `REGULAR`/`ONCE_PER_TURN` > `ONCE_PER_DRIVE` > `ONCE_PER_HALF` > `ONCE_PER_GAME`. |
| R8 | Where more than one skill could be used, build a **new** dialog + handler + parameter classes, templated on the existing `DialogReRollProperties*` classes. |
| R9 | If the coach selects skills that guarantee success, mark them used **immediately** and continue on the success path. All of the active player's modifiers must be declared *before* the Diving Tackle decision — there is no deferred or conditional commitment. |
| R10 | Even when the dodge will succeed, still offer Diving Tackle to the opposing coach, with a message making clear the dodge will succeed anyway. |
| R11 | If the coach selects a re-roll, route it through the **existing** re-roll handling. After a failed re-roll, re-run the modifier check and prompt again. |
| R12 | The move-square preview (`UtilServerPlayerMove`) must take the potential optional modifiers into account. |
| R13 | The dialog must show, for **each** available skill set, the roll that would have been needed with that set applied. |
| R14 | No behavioural change to code shared with the other rulesets. |


---

## 2. Current state (what we are replacing)

### 2.1 Break Tackle is auto-included

`ffb-common/.../skill/bb2025/BreakTackle.java:27-47` registers three `DodgeModifier`s
(`-1`/`-2`/`-3` by ST `3-`/`4`/`5+`) whose `appliesToContext` is:

```java
return <st condition> && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
```

The `|| hasUnusedSkill(...)` clause means `DodgeModifierFactory.findModifiers(...)` **speculatively
includes** Break Tackle whenever it is unused. Every consumer therefore has to implement the same
"compute with the strength modifier, then strip it and recompute, then decide" dance:

* `ffb-server/.../step/bb2025/move/StepMoveDodge.java:367-407` (success branch and failure branch)
* `ffb-server/.../step/bb2025/move/StepMoveDodge.java:514-519` (consumption)
* `ffb-server/.../skillbehaviour/bb2025/DivingTackleBehaviour.java:76-131` (prompt text)
* `ffb-server/.../skillbehaviour/bb2025/DivingTackleBehaviour.java:141-160` (consumption)

Identification is done via `DodgeModifier::isUseStrength`, i.e. Break Tackle specifically.

### 2.2 The "modifying skill" path is a one-skill special case

`usingModifyingSkill` / `USING_MODIFYING_SKILL` / `StatBasedRollModifier` /
`NamedProperties.canAddStrengthToDodge` implement a *single* optional post-roll modifier.
In BB2020 the only holder is `Incorporeal` (`skill/bb2020/special/Incorporeal.java:24-25`).

**In BB2025 no skill has `canAddStrengthToDodge` at all** — `skill/bb2025/special/Incorporeal.java`
is a completely different skill (`canAvoidDodging`), and there is no
`skill/bb2025/special/ConsummateProfessional.java`. Consequently:

* `StepMoveDodge.java:430-441` (the "rescue the dodge with BT before offering the DT re-roll" branch)
  is **dead code** in BB2025, and is additionally wrong: it gates on `canAddStrengthToDodge` but then
  sets `fUsingBreakTackle = true` and calls `markSkillUsed(canAddStrengthToDodge)` — it consumes the
  wrong resource.
* `DivingTackleBehaviour.java:85-118` builds messages around a stat-based modifier that never exists.

### 2.3 Known bugs to be removed by this rework

* **B1** — `StepMoveDodge.java:430-438`: Incorporeal/Break Tackle conflation described above.
* **B2** — `DivingTackleBehaviour.java:97`: `dodgeModifiers.addAll(modifierFactory.forType(DIVING_TACKLE))`
  adds the DT modifier to the wrong set; `requiredRoll` on line 98 is computed from
  `dodgeModifiersWithBT`, which never receives the DT `-2`. The "will force the use of BREAK TACKLE"
  hint is computed against a roll that is 2 too generous.
* **B3** — `DivingTackleBehaviour.java:129`: when DT would not change the outcome, the defending coach
  is never asked, so they cannot use DT purely to place the tackler prone in the vacated square.
  R10 fixes this.

---

## 3. Target design

### 3.1 Opt-in modifier selection carried on `DodgeContext`

`DodgeContext` gains a set of explicitly selected skills. `isUseBreakTackle()` is **kept** so that
`bb2016` and `bb2020` `BreakTackle` are unaffected.

`ffb-common/src/main/java/com/fumbbl/ffb/modifiers/DodgeContext.java`

```java
private final Set<Skill> selectedSkills;   // never null, defaults to emptySet()

public DodgeContext(Game game, ActingPlayer actingPlayer, FieldCoordinate source,
                    FieldCoordinate target, Set<Skill> selectedSkills) { ... }

public boolean isSkillSelected(Skill skill) {
    return selectedSkills.contains(skill);
}
```

Existing 4-arg and 5-arg (`boolean useBreakTackle`) constructors remain, delegating with an empty
selection set.

### 3.2 Marking a `DodgeModifier` as optional

`ffb-common/src/main/java/com/fumbbl/ffb/modifiers/DodgeModifier.java` gains a `boolean optional`
flag plus `isOptional()`, defaulting to `false` in all existing constructors.

> `useStrength` **must be kept**: it is consumed by `mechanics/bb2016/AgilityMechanic.java:45,120`
> and `ffb-client-logic/.../report/DodgeRollMessage.java:38`. `optional` is a new, orthogonal flag.

### 3.3 BB2025 skills

`ffb-common/src/main/java/com/fumbbl/ffb/skill/bb2025/BreakTackle.java` — drop the
`|| UtilCards.hasUnusedSkill(...)` clause and mark the modifiers optional:

```java
registerModifier(new DodgeModifier("Break Tackle ST 5+", "Break Tackle", -3, ModifierType.REGULAR,
        true /*useStrength*/, true /*optional*/) {
    @Override
    public boolean appliesToContext(Skill skill, DodgeContext context) {
        return context.getPlayer().getStrengthWithModifiers() >= 5 && context.isSkillSelected(skill);
    }
});
// ... ST 4 (-2) and ST 3- (-1) analogous
```

**New** `ffb-common/src/main/java/com/fumbbl/ffb/skill/bb2025/special/ConsummateProfessional.java`:

```java
@RulesCollection(Rules.BB2025)
public class ConsummateProfessional extends Skill {
    public ConsummateProfessional() {
        super("Consummate Professional", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
    }

    @Override
    public void postConstruct() {
        registerModifier(new DodgeModifier("Consummate Professional", -1, ModifierType.REGULAR,
                false /*useStrength*/, true /*optional*/) {
            @Override
            public boolean appliesToContext(Skill skill, DodgeContext context) {
                return context.isSkillSelected(skill);
            }
        });
    }
}
```

> Dodge modifiers are stored inverted (a `+3` bonus is `-3`), matching `BreakTackle`. A `+1`
> modifier is therefore `-1`.

### 3.4 New shared discovery helper: `OptionalDodgeModifierService`

New file: `ffb-common/src/main/java/com/fumbbl/ffb/modifiers/OptionalDodgeModifierService.java`
(instantiable, instance methods, per `AGENTS.md`).

This is **generic infrastructure, not ruleset behaviour** — it only ever returns anything for skills
that declare `DodgeModifier.isOptional()`, which is BB2025-only. `bb2016`/`bb2020` are unaffected
because none of their skills set the flag.

```java
List<OptionalDodgeModifier> availableFor(Game game, ActingPlayer actingPlayer,
                                          FieldCoordinate from, FieldCoordinate to);
```

Walk `actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes()`; for each skill collect its
`getDodgeModifiers()` entries where `isOptional()` and `!actingPlayer.isSkillUsed(skill)` and the
modifier's non-selection preconditions hold (e.g. the ST band). To evaluate the ST band without
duplicating skill logic, probe with a `DodgeContext` whose selection set contains exactly that skill
and check whether the modifier is returned by the factory.
`OptionalDodgeModifier` is a small holder of `{ Skill skill, DodgeModifier modifier }`.

It lives in `ffb-common` because it is needed both by the server-side selection service (§3.5) and by
the BB2025 `AgilityMechanic` preview hook (§3.7).

### 3.5 New server service: `DodgeModifierSelectionService`

New file: `ffb-server/src/main/java/com/fumbbl/ffb/server/util/bb2025/DodgeModifierSelectionService.java`
(instantiable, instance methods). Used by both `StepMoveDodge` (bb2025) and
`DivingTackleBehaviour` (bb2025).

1. **Enumerate viable options**:
   ```java
   List<DodgeModifierOption> findOptions(Game game, ActingPlayer actingPlayer,
                                          FieldCoordinate from, FieldCoordinate to,
                                          Set<DodgeModifier> extraModifiers, // e.g. DIVING_TACKLE
                                          int dodgeRoll);
   ```
   * Take the available modifiers from `OptionalDodgeModifierService.availableFor(...)`.
   * Enumerate all non-empty subsets (n is 0–3 in practice; guard with a hard cap of e.g. 5 to keep
     it `2^n` safe).
   * For each subset build `DodgeContext(..., subsetSkills)`, run
     `DodgeModifierFactory.findModifiers(...)`, add `extraModifiers`, and evaluate
     `minimumRoll = AgilityMechanic.minimumRollDodge(...)` +
     `DiceInterpreter.isSkillRollSuccessful(dodgeRoll, minimumRoll)`.
   * Keep only subsets that **guarantee success**, and **record each subset's `minimumRoll` on the
     resulting `DodgeModifierOption`** so the dialog can display, per option, the roll that would
     have been needed (see §4.3).

2. **Prune and rank** (R7):
   * Discard any subset that has a strict subset which also succeeds (minimality).
   * Rank remaining options by, in order: subset size ascending, then summed usage cost ascending,
     then skill name for stability. Usage cost:
     ```
     REGULAR, ONCE_PER_TURN, ONCE_PER_TURN_BY_TEAM_MATE  -> 0
     ONCE_PER_DRIVE                                      -> 1
     ONCE_PER_HALF                                       -> 2
     ONCE_PER_GAME, SPECIAL                              -> 3
     ```
     Add a `usageCost(SkillUsageType)` helper on the service (do **not** put ordering into the
     `SkillUsageType` enum — it is shared by all rulesets).
   * When two options are equivalent in size and total modifier value, keep only the cheaper one.

4. **Describe** an option for the dialog and for report text: `"Break Tackle"`,
   `"Consummate Professional"`, `"Break Tackle + Consummate Professional"`.

### 3.6 New common model: `DodgeModifierOption`

New file: `ffb-common/src/main/java/com/fumbbl/ffb/model/DodgeModifierOption.java`,
implementing `IJsonSerializable`.

Fields: `List<Skill> skills`, `int totalModifier`, `int minimumRoll`, `String label`.

`minimumRoll` is the roll that would have been required **with this option's skills applied** (and
including the Diving Tackle `-2` when the option was computed from the DT look-ahead). It is what the
dialog renders per button (R: "it has to show what would be needed to roll for each available set of
skills that could be used").

JSON: serialise skills by name and resolve via `source.getRules().getSkillFactory()`, mirroring how
`DialogReRollPropertiesParameter:112-113,140-142` handles `reRollProperties`.

### 3.7 Move-square preview (`UtilServerPlayerMove`)

`ffb-server/.../util/UtilServerPlayerMove.addMoveSquare` (line 165) currently builds a `DodgeContext`
inline to fill `MoveSquare.minimumRollDodge`. Because Break Tackle is auto-included today, the
preview already shows the Break-Tackle-improved number; with opt-in modifiers the naive result would
regress to the unmodified number.

`UtilServerPlayerMove` is shared by all rulesets, so the fix must not change `bb2016`/`bb2020`
behaviour. Dispatch through the **already ruleset-scoped** `AgilityMechanic` instead:

`ffb-common/src/main/java/com/fumbbl/ffb/mechanics/AgilityMechanic.java` — add

```java
public abstract int minimumRollDodgePreview(Game game, ActingPlayer actingPlayer,
                                             FieldCoordinate from, FieldCoordinate to);
```

* `mechanics/bb2016/AgilityMechanic` and `mechanics/bb2020/AgilityMechanic` implement it by
  replicating the current inline logic verbatim:
  ```java
  Set<DodgeModifier> modifiers = game.<DodgeModifierFactory>getFactory(Factory.DODGE_MODIFIER)
      .findModifiers(new DodgeContext(game, actingPlayer, from, to));
  return minimumRollDodge(game, actingPlayer.getPlayer(), modifiers);
  ```
  → **behaviourally identical to today** for those rulesets.
* `mechanics/bb2025/AgilityMechanic` additionally selects every currently available optional modifier
  via `OptionalDodgeModifierService.availableFor(...)` and computes the **best achievable** roll:
  ```java
  Set<Skill> allOptional = optionalService.availableFor(game, actingPlayer, from, to)
      .stream().map(OptionalDodgeModifier::getSkill).collect(toSet());
  Set<DodgeModifier> modifiers = factory.findModifiers(new DodgeContext(game, actingPlayer, from, to, allOptional));
  return minimumRollDodge(game, actingPlayer.getPlayer(), modifiers);
  ```

`UtilServerPlayerMove.addMoveSquare` then becomes a one-line delegation:
`minimumRollDodge = mechanic.minimumRollDodgePreview(game, actingPlayer, playerCoordinate, pCoordinate);`
— a shared-file edit, but a pure dispatch with byte-identical results for `bb2016`/`bb2020`.

> `MoveSquare` (`ffb-common/src/main/java/com/fumbbl/ffb/MoveSquare.java`) carries a single
> `minimumRollDodge` int, so the preview shows the best achievable roll. Showing *both* the
> unmodified and the best achievable roll would require extending `MoveSquare` and the client
> rendering; see §7 D1.

---

## 4. New dialog (R8)

Templated on `DialogReRollProperties*`. The re-roll half of the dialog reuses `ReRollOptions`
(`ffb-common/.../ReRollOptions.java`), which is already `IJsonSerializable` + `HasReRollProperties`
and is produced by the already-public
`ffb-server/.../mechanic/bb2025/RollMechanic.findReRollOptions(...)` (line 615).

### 4.1 New / changed files

| Module | File | Action |
|---|---|---|
| ffb-common | `dialog/DialogDodgeModifierChoiceParameter.java` | **new** — implements `IDialogParameter`, `HasReRollProperties` |
| ffb-common | `dialog/DialogId.java` | add `DODGE_MODIFIER_CHOICE("dodgeModifierChoice")` |
| ffb-common | `dialog/DialogParameterFactory.java` | add `case DODGE_MODIFIER_CHOICE` |
| ffb-common | `model/DodgeModifierOption.java` | **new** |
| ffb-common | `json/IJsonOption.java` | add `DODGE_MODIFIER_OPTIONS` (json array), `SELECTED_SKILLS` (string array) |
| ffb-common | `net/commands/ClientCommandDodgeModifierChoice.java` | **new** |
| ffb-common | `net/NetCommandId.java` | add `CLIENT_DODGE_MODIFIER_CHOICE("clientDodgeModifierChoice")` + factory case (~line 263) |
| ffb-client-logic | `client/dialog/DialogDodgeModifierChoice.java` | **new** — Swing dialog |
| ffb-client-logic | `client/dialog/DialogDodgeModifierChoiceHandler.java` | **new** |
| ffb-client-logic | `client/dialog/DialogManager.java` | add `case DODGE_MODIFIER_CHOICE` (~line 45) |
| ffb-client-logic | `client/net/ClientCommunication.java` | add `sendDodgeModifierChoice(String playerId, List<Skill> skills, ReRolledAction action)` |

### 4.2 `DialogDodgeModifierChoiceParameter`

```java
public class DialogDodgeModifierChoiceParameter implements IDialogParameter, HasReRollProperties {
    private String playerId;
    private ReRolledAction reRolledAction;
    private int minimumRoll;              // required roll WITHOUT any optional modifier
    private int dodgeRoll;                // the die that was actually rolled
    private boolean fumble;
    private List<DodgeModifierOption> modifierOptions;   // each carries its own minimumRoll
    private List<ReRollProperty> reRollProperties;   // from ReRollOptions
    private Skill reRollSkill;                       // from ReRollOptions
    private List<String> messages;                   // context, e.g. the Diving Tackle warning
    private CommonProperty menuProperty;
    private String defaultValueKey;
    ...
    public DialogId getId() { return DialogId.DODGE_MODIFIER_CHOICE; }
}
```

### 4.3 `DialogDodgeModifierChoice` (client)

Layout mirrors `DialogReRollProperties.java`:

* Title `"Use a Skill or a Re-roll"`.
* Info panel with the dice icon, the base message
  (`"You rolled a <dodgeRoll> and needed <minimumRoll>+ to succeed."`), the `messages` list, the
  LONER warning (`hasProperty(ReRollProperty.LONER)`), and the fumble line.
* **One button per `DodgeModifierOption`, labelled `"<label> (<option.minimumRoll>+)"`**, e.g.
  `"Break Tackle (4+)"`, `"Consummate Professional (5+)"`,
  `"Break Tackle + Consummate Professional (3+)"`. This is the per-option required roll produced in
  §3.5, so the coach can see exactly what each combination buys before committing a
  once-per-game skill. Mnemonics assigned from a fixed pool (`1..9`) to avoid clashing with the
  re-roll mnemonics `T`/`F`/`P`/`S`/`N`.
* The full re-roll button block copied verbatim from `DialogReRollProperties`, including the
  `DialogExtensionMascot` wrapper, `PRO`/`TRR`/`MASCOT` fallback checkboxes and
  `determineProReRollSource()`.
* `"No Re-Roll"` / decline button.

Exposes `getSelectedOption()`, `getReRollSource()`, `isUseModifiers()`.

### 4.4 `DialogDodgeModifierChoiceHandler`

`dialogClosed(IDialog)` mirrors `DialogReRollPropertiesHandler:52-62`:

```java
if (dialog.isUseModifiers()) {
    communication.sendDodgeModifierChoice(playerId, dialog.getSelectedOption().getSkills(), reRolledAction);
} else {
    communication.sendUseReRoll(reRolledAction, dialog.getReRollSource());   // may be null = decline
}
```

**Sending `ClientCommandUseReRoll` for the re-roll branch is deliberate** — it satisfies R11 by
reusing `AbstractStepWithReRoll`'s existing command handling with zero server-side changes.

`showDialog()` mirrors the waiting-status path for the non-owning client.

---

## 5. Server-side rework

### 5.1 `ffb-server/.../step/bb2025/move/StepMoveDodge.java`

**Removed state:** `fUsingBreakTackle`, `usingModifyingSkill`, `dtRerollAsked`.
**New state:**

| Field | Meaning | JSON |
|---|---|---|
| `Set<Skill> selectedModifierSkills` | selected by the coach and immediately marked used | `SELECTED_MODIFIER_SKILLS` |
| `boolean modifierChoiceOffered` | replaces `dtRerollAsked`; suppresses re-prompting | `MODIFIER_CHOICE_OFFERED` |

> **Modifiers are always committed immediately.** The rules require every modifier used by the active
> player to be declared *before* the Diving Tackle decision is made, so there is no provisional or
> deferred state: whenever the coach picks an option — including in the pre-Diving-Tackle prompt of
> §5.2 — the skills are marked used on the spot and published downstream. A defender who then
> declines Diving Tackle does not refund them.


New step parameter keys (`ffb-server/.../step/StepParameterKey.java`) and JSON options
(`ffb-server/.../server/IServerJsonOption.java`):
`SELECTED_DODGE_MODIFIER_SKILLS` (published downstream to `StepDivingTackle` and the retry
`StepMoveDodge`). `USING_BREAK_TACKLE` and `USING_MODIFYING_SKILL` are no longer published by the
bb2025 step — they must remain in the enums because `bb2016`/`bb2020` still use them.

**`handleCommand`** gains a `NetCommandId.CLIENT_DODGE_MODIFIER_CHOICE` branch that stores the chosen
skills and returns `StepCommandStatus.EXECUTE_STEP`. The `canAddStrengthToDodge` branch at
lines 176-184 is deleted. `canChooseToIgnoreDodgeModifierAfterRoll` and `canRerollDodge` branches
are retained unchanged.

**`dodge(boolean pDoRoll)` restructured:**

```
1. if (pDoRoll) roll and publish DODGE_ROLL
2. modifiers = factory.findModifiers(new DodgeContext(game, actingPlayer, from, to, selectedModifierSkills))
   (empty on the first pass => R3)
3. minimumRoll = mechanic.minimumRollDodge(game, player, modifiers)
   successful = DiceInterpreter.isSkillRollSuccessful(dodgeRoll, minimumRoll)
4. report ReportDodgeRoll (only when pDoRoll)
5. if (successful)  -> divingTackleLookAhead()   // §5.2
   else             -> offerRescue(modifiers, minimumRoll, /*divingTackle*/ false)
```

**`offerRescue(baseModifiers, minimumRoll, dueToDivingTackle)`** — the single shared implementation
of R5/R6/R11:

```
options   = selectionService.findOptions(..., extraModifiers, fDodgeRoll)
rrOptions = rollMechanic.findReRollOptions(gameState, player, ReRolledActions.DODGE,
                                            uncanceledDodgeRerollSource(...))

if (options.isEmpty() && !rrOptions.canActuallyReRoll())  -> failDodge() / return FAILURE
if (options.isEmpty())                                     -> existing UtilServerReRoll.askForReRollIfAvailable(...)
else  -> UtilServerDialog.showDialog(new DialogDodgeModifierChoiceParameter(...))
         modifierChoiceOffered = true
         return WAITING_FOR_RE_ROLL
```

Note `uncanceledDodgeRerollSource(...)` (lines 564-576) is preserved unchanged — skill re-roll
sources cancelled by an adjacent opponent must still not be offered.

**On the modifier-choice reply:** add the chosen skills to `selectedModifierSkills`,
`markSkillUsed(...)` each **immediately** (R9), emit one
`ReportSkillUse(playerId, skill, true, SkillUse.ADD_DODGE_MODIFIER)` per skill, publish
`SELECTED_DODGE_MODIFIER_SKILLS`, then re-enter `dodge(false)` — no new die.


**On the re-roll reply:** unchanged; `AbstractStepWithReRoll` handles it, `dodge(true)` rolls a new
die, and the failure branch calls `offerRescue(...)` again — the optional skills are still unused, so
they are offered again (R11).

**Deletions:** lines 367-407 (the BT strip/re-add dance), 430-441 (B1), 514-519 (BT consumption),
`showUseModifyingSkillDialog(...)`, `getModifyingSkillInCaseItHelps(...)`, and all
`StatBasedRollModifier` handling. `usingModifierIgnoringSkill` /
`canChooseToIgnoreDodgeModifierAfterRoll` handling (lines 320-340, 471-476, 505-511) is **kept** —
it is a separate mechanic.

### 5.2 Diving Tackle look-ahead (replaces `StepMoveDodge.java:416-456`)

```
divingTacklers = UtilPlayer.findEligibleDivingTacklers(game, from, to, canAttemptToTackleDodgingPlayer)
if (divingTacklers.isEmpty() || modifierChoiceOffered) return SUCCESS

withDt = modifiers + modifierFactory.forType(ModifierType.DIVING_TACKLE)
if (roll still succeeds with withDt) return SUCCESS      // DT cannot hurt; DT step still prompts (R10)

// The dodge would fail because of Diving Tackle -> same treatment as a real failure (R5)
return offerRescue(withDt, minimumRollWithDt, /*dueToDivingTackle*/ true)
```

with the dialog message
`"Diving Tackle can make this dodge fail."` prepended to the message list.

### 5.3 `ffb-server/.../skillbehaviour/bb2025/DivingTackleBehaviour.java`

Rewritten. `state.usingBreakTackle` and `state.usingModifyingSkill` are no longer read; a new
`StepState` field `Set<Skill> selectedModifierSkills` is used instead
(`StepDivingTackle.StepState` is a plain bag; the existing fields stay for `bb2016`/`bb2020`).

`ffb-server/.../step/action/move/StepDivingTackle.java` (`COMMON`) changes:

* `StepState` gains `public Set<Skill> selectedModifierSkills;`
* `setParameter` gains a `SELECTED_DODGE_MODIFIER_SKILLS` case
* `toJsonValue` / `initFrom` serialise it

Behaviour, first entry (`state.usingDivingTackle == null`):

```
if (dodger still occupies coordinateFrom) return       // dodge failed, nothing to do
divingTacklers = UtilPlayer.findEligibleDivingTacklers(...)
if (divingTacklers.isEmpty() || dodgeRoll <= 0) return

modifiers   = findModifiers(new DodgeContext(..., state.selectedModifierSkills))
withDt      = modifiers + forType(DIVING_TACKLE)
tripsDodger = !isSkillRollSuccessful(dodgeRoll, minimumRollDodge(withDt))

descriptions = [ tripsDodger
                   ? "This will trip the dodger."
                   : selectedModifierSkills.isEmpty()
                       ? "This will NOT trip the dodger, the dodge will still succeed."
                       : "This will NOT trip the dodger, but will force the use of " + labelOf(selected) + "." ]

promptForDT(...)      // ALWAYS prompt (R10) - removes the WOULD_NOT_HELP short-circuit (B3)
```

This deletes the entire message-derivation block at lines 85-118, taking bug **B2** with it, and
deletes the `strengthModifier` / `strengthSkill` consumption block at lines 141-160 (Break Tackle is
now committed by `StepMoveDodge`, not here).

Second entry (`state.usingDivingTackle != null`) keeps its current shape:

* publish `USING_DIVING_TACKLE`
* if used: report `ReportSkillUse(defenderId, skill, true, STOP_OPPONENT)`,
  `GOTO_LABEL state.goToLabelOnSuccess`
* if not used: `NEXT_STEP`

No skill consumption happens here at all — the active player's modifiers were already declared and
marked used by `StepMoveDodge` before this step ran.


### 5.4 Retry `StepMoveDodge` (`@RETRY_DODGE`)

Receives `SELECTED_DODGE_MODIFIER_SKILLS`, `USING_DIVING_TACKLE = true`, `DODGE_ROLL` and
`MODIFIER_CHOICE_OFFERED = true`. It re-evaluates the same die with DT added and the committed
skills applied; because `modifierChoiceOffered` is true it does **not** prompt again (the coach
already made the call before DT was declared — see §7 D2).

### 5.5 Reports

Add to `ffb-common/src/main/java/com/fumbbl/ffb/SkillUse.java`:

```java
ADD_DODGE_MODIFIER("addDodgeModifier", "to improve the dodge roll"),
```

`ReportDodgeRoll` already carries the modifier array, so the selected modifiers render automatically
in the game log. Verify `ffb-client-logic/.../report/DodgeRollMessage.java:38` still behaves — it
branches on `isUseStrength`, which is unchanged.

---

## 6. Collateral impact to verify

* **Ruleset isolation.** The only shared files touched are `DodgeModifier` (additive `optional` flag,
  default `false`), `DodgeContext` (additive selection set, existing constructors preserved),
  `AgilityMechanic` (new abstract `minimumRollDodgePreview`, implemented per ruleset), and
  `UtilServerPlayerMove.addMoveSquare` (pure dispatch). `bb2016` and `bb2020` must produce identical
  results before and after — assert this with the regression tests in §9.
* **`ReRollSources.CONSUMMATE_PROFESSIONAL`** and
  `ClientCommandUseConsummateReRollForBlock` / `step/bb2025/block/StepBlockRoll.java:125-134`:
  BB2025 blocks already reference a Consummate Professional re-roll. Adding the skill to the BB2025
  skill factory may make that path reachable for the first time — check whether the block path
  expects the skill to also register a re-roll source, and whether that must be added to the new
  BB2025 skill class.
* Star player position definitions that grant Consummate Professional must resolve the skill in the
  BB2025 rules collection.

---

## 7. Decisions (resolved)

**D1 — Should the move-square preview show one number or two?**
§3.7 makes the preview show the **best achievable** roll (all available optional modifiers applied),
which is the closest match to today's behaviour since Break Tackle is currently auto-included. The
downside is that the preview no longer shows the roll the coach will actually face if they decline to
spend anything. Showing both would mean extending `MoveSquare` with a second int plus client
rendering changes — a shared-model change affecting all rulesets.
*Recommendation:* single best-achievable number now; revisit if coaches find it misleading.
**Decided:** one number, the best achievable roll of all available combinations. Implemented in
`bb2025/AgilityMechanic.minimumRollDodgePreview`.

**D2 — Re-prompt after Diving Tackle is actually declared?**
`modifierChoiceOffered` suppresses a second prompt in the retry `StepMoveDodge`. This follows from
the rule that all of the active player's modifiers are declared before the Diving Tackle decision,
and mirrors the existing `dtRerollAsked` design. Confirm no BB2025 corner case re-opens the choice.
**Decided:** do not re-prompt, the plan is correct. `modifierChoiceOffered` is published to the retry
step and only reset when a new die is rolled.

**D3 — Dialog when only a single one-skill option exists.**
The plan always uses the new dialog when at least one modifier option exists, and falls back to the
existing `DialogReRollProperties` only when there are no modifier options. R8 says "for the cases
where more than one skill could be used" — reusing `DialogSkillUseParameter` for the single-skill
case would honour that more literally but doubles the number of code paths.
*Recommendation:* one dialog for all modifier cases.
**Decided:** follow the recommendation, a single path for all modifier cases.


---

## 8. Work breakdown

| # | Task | Modules | Done |
|---|---|---|---|
| 1 | `DodgeModifier.optional` flag; `DodgeContext.selectedSkills` + `isSkillSelected` | ffb-common | ✅ |
| 2 | `OptionalDodgeModifierService` + `OptionalDodgeModifier` holder | ffb-common | ✅ |
| 3 | `DodgeModifierOption` model (incl. `minimumRoll`) + JSON | ffb-common | ✅ |
| 4 | BB2025 `BreakTackle` opt-in; new BB2025 `ConsummateProfessional` | ffb-common | ✅ |
| 5 | `AgilityMechanic.minimumRollDodgePreview` + bb2016/bb2020 (verbatim) and bb2025 (best achievable) implementations | ffb-common | ✅ |
| 6 | `UtilServerPlayerMove.addMoveSquare` delegates to the mechanic | ffb-server | ✅ |
| 7 | `SkillUse.ADD_DODGE_MODIFIER`; new `IJsonOption`s | ffb-common | ✅ |
| 8 | `DialogId`, `DialogParameterFactory`, `DialogDodgeModifierChoiceParameter` | ffb-common | ✅ |
| 9 | `NetCommandId` + `ClientCommandDodgeModifierChoice` | ffb-common | ✅ |
| 10 | `DodgeModifierSelectionService` (subset enumeration, per-option `minimumRoll`, pruning, ranking) | ffb-server | ✅ |
| 11 | `StepParameterKey.SELECTED_DODGE_MODIFIER_SKILLS` + `IServerJsonOption` entries | ffb-server | ✅ |
| 12 | `StepDivingTackle.StepState.selectedModifierSkills` + param/JSON plumbing | ffb-server | ✅ |
| 13 | Rewrite `step/bb2025/move/StepMoveDodge` (§5.1, §5.2) | ffb-server | ✅ |
| 14 | Rewrite `skillbehaviour/bb2025/DivingTackleBehaviour` (§5.3) | ffb-server | ✅ |
| 15 | `DialogDodgeModifierChoice` + handler + `DialogManager` + `ClientCommunication` | ffb-client-logic | ✅ |
| 16 | Change list entry in the `3.4.0` `VersionChangeList` | ffb-client-logic | ✅ |
| 17 | Tests (§9) | ffb-common, ffb-server, ffb-client-logic | ✅ |


Build order: `ffb-common` → `ffb-server` → `ffb-client-logic` → `ffb-client`.

---

## 9. Testing

**`DodgeModifierSelectionServiceTest`** (ffb-server, JUnit 5 + Mockito):

* No optional skills → no options.
* Break Tackle alone rescues → single option `[Break Tackle]`.
* Consummate Professional alone rescues → single option `[Consummate Professional]`.
* Both needed → single option `[Break Tackle, Consummate Professional]`; the two single-skill subsets
  are excluded because they do not succeed.
* Either alone rescues, both are `ONCE_PER_GAME`/`ONCE_PER_TURN` → the `ONCE_PER_TURN` skill ranks
  first (R7).
* Superset pruning: `[BT]` succeeds → `[BT, CP]` is discarded.
* Already-used skill is not offered.
* Diving Tackle `extraModifiers` shift the threshold correctly.
* Each returned option carries the correct `minimumRoll` for its own skill set.

**`AgilityMechanicPreviewTest`** (ffb-common) — regression guard for §3.7:

* bb2016 and bb2020 `minimumRollDodgePreview` return exactly what the old inline
  `UtilServerPlayerMove` computation returned (including Break Tackle auto-inclusion).
* bb2025 returns the roll with all available optional modifiers applied, and ignores skills that are
  already used.

**`DialogDodgeModifierChoiceTest`** (ffb-client-logic) — model on the existing
`ffb-client-logic/src/test/java/com/fumbbl/ffb/client/dialog/DialogReRollPropertiesTest.java`:
button visibility per `ReRollProperty`, one button per option, per-option label shows the option's
own `minimumRoll`, correct command on close.

**JSON round-trip** tests for `DialogDodgeModifierChoiceParameter`, `DodgeModifierOption`,
`ClientCommandDodgeModifierChoice`, and the extended `StepMoveDodge` / `StepDivingTackle` step state
(step serialisation matters for replay and reconnect).

**Implemented automated tests:**

* `ffb-server/src/test/java/com/fumbbl/ffb/server/util/bb2025/DodgeModifierSelectionServiceTest.java` ✅
* `ffb-common/src/test/java/com/fumbbl/ffb/mechanics/AgilityMechanicDodgePreviewTest.java` ✅
  (named `AgilityMechanicDodgePreviewTest`, covers the §3.7 regression guard for all three rulesets)
* `ffb-common/src/test/java/com/fumbbl/ffb/json/DodgeModifierChoiceJsonTest.java` ✅
  (round trips for `DodgeModifierOption`, `DialogDodgeModifierChoiceParameter`,
  `ClientCommandDodgeModifierChoice`)
* `ffb-client-logic/src/test/java/com/fumbbl/ffb/client/dialog/DialogDodgeModifierChoiceTest.java` ✅
  (visual harness, same style as `DialogReRollPropertiesTest`)
* `ffb-statetest/src/test/java/com/fumbbl/ffb/test/skill/move/DodgeModifierChoiceTest.java` ✅
  (step flow: no dialog on success, both single-skill options offered on failure, chosen skill
  rescues the dodge and is marked used; covers scenarios 1, 4 and 5 below)

**Manual / scripted scenarios:** (still to be verified by hand)

1. Dodge succeeds outright, no DT adjacent → no dialogs, no skills used.
2. Dodge succeeds outright, DT adjacent, DT cannot change the result → defender is still prompted
   with *"This will NOT trip the dodger, the dodge will still succeed."* (R10, B3), and using DT
   still places the tackler prone.
3. Dodge fails, only a TRR available → existing `DialogReRollProperties`.
4. Dodge fails, only Break Tackle rescues → new dialog with one skill button + TRR; picking the
   skill succeeds without a new roll, and the button showed the correct required roll.
5. Dodge fails, BT and CP both individually rescue → two single-skill buttons with their own required
   rolls; CP (once per game) ranked last.
6. Dodge fails, only BT + CP together rescue → one combined button.
7. Dodge succeeds but would fail with DT → pre-DT dialog; coach commits BT → BT is marked used
   **immediately**; defender then declines DT → BT stays used (declared before the DT decision).
8. Dodge fails → coach takes the TRR → re-roll also fails → modifier dialog appears again (R11).
9. Move-square preview on a player with unused BT and/or CP shows the best achievable dodge roll;
   the same player with both skills already used shows the unmodified roll.
10. Reconnect / replay in the middle of an open modifier dialog.


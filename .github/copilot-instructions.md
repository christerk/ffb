# Copilot repository instructions

## Default ruleset
- Treat every task in this repository as targeting the 2025 ruleset by default.
- Apply a different ruleset only when the task explicitly specifies one; that task-level instruction takes precedence over the 2025 default.
- When implementation requires ruleset-specific code, prefer existing `bb2025` packages, factories, mechanics, and tests unless the task states otherwise.
- Never use inheritance between ruleset classes (e.g. a `bb2025` step extending a `bb2020` or `mixed` step). Duplicate the class into each affected ruleset package and keep each ruleset's logic self-contained. If a shared `mixed` class ends up used by only one ruleset after such a change, move it to that ruleset's package and leave the other rulesets' logic untouched.

## Services
- Prefer instantiable service classes with instance methods over static utility classes/methods; create a service instance at the call site instead of calling static methods.

## Naming
- Do not use legacy `f` or `p` prefixes for new fields or parameters.

## Change list entries
- Add a user-facing change list entry for any user-visible change. Entries live in the top (latest) `VersionChangeList` in `ffb-client-logic/src/main/java/com/fumbbl/ffb/client/model/ChangeList.java`, added via `addBugfix`/`addImprovement`/`addFeature`/`addBehaviorChange`.
- Do not mention the rules version in entries for the 2025 ruleset, it is the default. Only name the rules version when the change is specific to the 2016 or 2020 ruleset.
- Do not add an entry for a fix of an issue that was introduced by the ongoing feature implementation itself. Only changes to already released behavior need an entry.

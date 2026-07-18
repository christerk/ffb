# Copilot repository instructions

## Default ruleset
- Treat every task in this repository as targeting the 2025 ruleset by default.
- Apply a different ruleset only when the task explicitly specifies one; that task-level instruction takes precedence over the 2025 default.
- When implementation requires ruleset-specific code, prefer existing `bb2025` packages, factories, mechanics, and tests unless the task states otherwise.

## Services
- Prefer instantiable service classes with instance methods over static utility classes/methods; create a service instance at the call site instead of calling static methods.

## Change list entries
- Add a user-facing change list entry for any user-visible change. Entries live in the top (latest) `VersionChangeList` in `ffb-client-logic/src/main/java/com/fumbbl/ffb/client/model/ChangeList.java`, added via `addBugfix`/`addImprovement`/`addFeature`/`addBehaviorChange`.

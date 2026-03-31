# Agent Guidelines

## Build Commands
- Build project: `mvn clean install`
- Run tests: `mvn test`
- Run single test: `mvn test -Dtest=TestClassName#testMethodName`
- Skip tests: `mvn install -DskipTests`

## Code Style
- Java version: 8
- Encoding: UTF-8
- Testing: JUnit 5 with Mockito for mocking
- Package naming: `com.fumbbl.*` 
- Project structure: Multi-module Maven project with `ffb-common`, `ffb-tools`, `ffb-server`, `ffb-client`, `ffb-client-logic`, and `ffb-resources`; most client logic lives in `ffb-client-logic`, while desktop/AWT-specific client code and packaging live in `ffb-client`
- Architecture hotspots: server game flow is organized around `Step` / `SequenceGenerator` classes under `ffb-server/src/main/java/com/fumbbl/ffb/server/step`, while client input/game-phase flow is organized around `ClientState` classes under `ffb-client-logic/src/main/java/com/fumbbl/ffb/client/state`
- Error handling: Use appropriate exception types, document exceptions in method signatures
- Test style: Test class names end with "Test", use descriptive test method names

### Import Organization
1. Project imports first (com.fumbbl.*)
2. Core Java imports after project imports (javax.*, java.*, etc.)
3. Within project imports, organize by package hierarchy
4. No wildcard imports (*) - list each class explicitly
5. Each import on its own line, no concatenation
6. Use blank lines to separate import groups

## Dependencies
Shared versions managed via root `pom.xml` currently include `classgraph`, JUnit 5, and Mockito. Many module-specific dependencies still declare versions in their own `pom.xml`, and `ffb-client` / `ffb-client-logic` also resolve bundled libraries from their module-local `repo/` directories.

## Build Order
1. ffb-common
2. ffb-tools
3. ffb-server
4. ffb-client-logic
5. ffb-resources
6. ffb-client

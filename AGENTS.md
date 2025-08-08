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
- Project structure: Multi-module Maven project with separate client/server/common modules
- Error handling: Use appropriate exception types, document exceptions in method signatures
- Test style: Test class names end with "Test", use descriptive test method names

## Dependencies
Dependencies are managed centrally in root pom.xml. Add new dependencies there first.

## Build Order
1. ffb-common
2. ffb-tools
3. ffb-server
4. ffb-client
5. ffb-client-logic
6. ffb-resources
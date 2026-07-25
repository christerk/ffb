# ffb-statetest - Testing Guide

## Overview

The `ffb-statetest` module tests server-side game logic by simulating client commands through the server step engine. Tests set up a game state, send commands (like selecting a player for a block action), and assert the resulting game state.

## Module Structure

```
ffb-statetest/
  src/main/java/com/fumbbl/ffb/test/
    TestServer.java          - Wraps FantasyFootballServer for test isolation
    GameStateBuilder.java    - DSL for building game state (players, skills, positions)
    StepEngine.java          - Utility to start steps and handle commands
    Commands.java            - Factory methods for ClientCommand objects
    TestRolls.java           - DSL for pre-configuring dice results
  src/test/java/com/fumbbl/ffb/test/
    BlockTest.java           - Existing test class (the pattern to follow)
```

## Dependency Setup

- `pom.xml` depends on `ffb-common`, `ffb-server`, JUnit 5, and Mockito (compile scope because test utilities need it at test time).
- The parent POM manages JUnit 5.5.0 and Mockito 4.11.0 (Java 8) / 5.23.0 (Java 21 with `-Pmockito5`).
- Build: `mvn clean install` (from root) or `mvn test -pl ffb-statetest` (from root).

## Test Pattern

Every test follows this basic structure:

### 1. Setup with `TestServer` + `GameStateBuilder`

```java
private TestServer testServer;

@BeforeEach
public void setUp() throws Exception {
    testServer = new TestServer();
}

@Test
public void myTest() {
    GameState state = new GameStateBuilder(testServer.getGameState())
        .withRule("BB2025")   // or "BB2020", "BB2016"
        .withTeam(true, t -> t            // home team
            .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
        .withTeam(false, t -> t           // away team
            .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
        .build();
```

### 2. Drive the Step Sequence

```java
// Start the game loop (pushes the Select sequence)
StepEngine.start(state);

// Send a command - it returns the current step after handling
IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
StepEngine.respond(state, Commands.block("home1", "away1"));
StepEngine.respond(state, Commands.blockChoice(0));   // index 0 = first die result
```

### 3. Pre-configure dice before the roll step

```java
// Declare expected rolls BEFORE the step that consumes them
TestRolls.on(state)
    .block("bothdown")    // block die result: bothdown, pushback, pow, stumble, playerdown
    .armor(2, 2)          // two general dice (values 1-6) for 2d6 armor roll
    .injury(4, 5);        // two general dice for 2d6 injury roll
```

### 4. Assert the final state

```java
Game game = state.getGame();
PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));

assertEquals(PlayerState.STANDING, attackerState.getBase());
assertTrue(defenderState.getBase() == PlayerState.PRONE ||
           defenderState.getBase() == PlayerState.STUNNED);
```

## DSL Reference

### `GameStateBuilder`

| Method | Description |
|--------|-------------|
| `withRule(String)` | Rules version: `"BB2025"`, `"BB2020"`, `"BB2016"` |
| `withTeam(boolean, Consumer<TeamDef>)` | `true` = home, `false` = away |
| `.player(String id, Consumer<PlayerDef>)` | Add a player to the team |
| `.at(int x, int y)` | Set player field coordinate |
| `.stats(int ma, int st, int ag, int pa, int av)` | Movement, Strength, Agility, Passing, Armour |
| `.skill(String name)` | Add a skill by name (e.g. `"Block"`, `"Dodge"`, `"Mighty Blow"`). Callable multiple times |

**Note:** Players are initialized as `STANDING` with `active=true`. The builder calls `registerBehaviours()`, `initRulesDependentMembers()`, and pushes the initial `Select` sequence.

### `Commands`

Common commands are in `Commands.java`. Add new ones as the module grows:

| Method | NetCommand class |
|--------|-----------------|
| `selectPlayer(id, action)` | `ClientCommandActingPlayer` |
| `block(attackerId, defenderId)` | `ClientCommandBlock` |
| `blockChoice(index)` | `ClientCommandBlockChoice` |
| `pushback(Pushback)` | `ClientCommandPushback` |
| `followup(boolean)` | `ClientCommandFollowupChoice` |

To add a new command, look up the corresponding `ClientCommand*` class in `ffb-common/src/main/java/com/fumbbl/ffb/net/commands/`, create the instance, and add a static factory method to `Commands.java`.

### `StepEngine`

```java
static IStep start(GameState)        // calls gameState.startNextStep()
static IStep respond(GameState, NetCommand) // calls gameState.handleCommand()
```

### `TestRolls`

| Method | Category | Description |
|--------|----------|-------------|
| `block(String...)` | `Block` | Block dice: `"playerdown"`/`"skull"`, `"bothdown"`, `"pushback"`, `"stumble"`, `"pow"` |
| `armor(int, int)` | `General` | Two individual dice (1-6) for 2d6 armor roll |
| `injury(int, int)` | `General` | Two individual dice (1-6) for 2d6 injury roll |
| `casualty(int)` | `General` | Single die for serious injury roll |
| `general(int...)` | `General` | Raw integer dice for any general roll |

**Important:** Rolls are consumed from a queue in FIFO order. Declare all expected rolls in order before the step that consumes them. When a roll is needed, the DiceRoller checks the category queue first; if empty, it uses `Fortuna` RNG (which is seeded deterministically in tests, but pre-declaring rolls is preferred for exact control).

## Dice Category System

Test roll strings are parsed by `DiceCategoryFactory`:
- **Block dice**: `BlockDiceCategory` accepts strings like `"pushback"`, `"bothdown"`, `"skull"`, `"pow"`, `"stumble"` (see `BlockDiceCategory.BlockEnums` for full aliases).
- **Direction dice**: `DirectionDiceCategory` accepts `"n"`, `"ne"`, `"e"`, `"se"`, `"s"`, `"sw"`, `"w"`, `"nw"` for scatter/pushback direction.
- **General dice**: Plain integer strings like `"2"`, `"5"` — parsed by `DiceCategory`.

## Step Flow for Common Actions

### Block Action (BB2025)

```
INIT_BLOCKING -> FOUL_APPEARANCE -> DUMP_OFF -> BLOCK_STATISTICS -> DAUNTLESS ->
BLOCK_ROLL -> BLOCK_CHOICE ->
  (SKULL)    -> DROP_FALLING_PLAYERS -> ... -> APOTHECARY
  (BOTHDOWN) -> JUGGERNAUT -> BOTHDOWN_STEP -> WRESTLE -> DROP_FALLING -> APOTHECARY
  (POW/PUSH) -> BLOCK_DODGE -> PUSHBACK -> APOTHECARY(CROWD/REGULAR) -> FOLLOWUP
```

**Block flow commands:**
1. `Commands.selectPlayer("playerId", PlayerAction.BLOCK)`
2. `Commands.block("attackerId", "defenderId")`
3. (If multiple dice) `Commands.blockChoice(index)` — index 0 = first die listed
4. If pushback: `Commands.pushback(new Pushback(playerId, new FieldCoordinate(x, y)))` then `Commands.followup(true/false)`

### Foul Action (BB2025)

```
INIT_FOULING -> FOUL -> BRIBES -> REFEREE -> EJECT_PLAYER -> END_FOULING
```

### Pass Action (BB2025)

```
INIT_PASSING -> ... -> PASS -> INTERCEPT -> HAIL_MARY_PASS/MISSED_PASS/RESOLVE_PASS -> END_PASSING
```

### Move Action (BB2025)

```
INIT_MOVING -> MOVE -> (repeated for each step) -> MOVE_DODGE -> END_MOVING
```

### Turn Flow

```
SELECT -> (player chooses action) -> action sequence -> END_SELECTING -> SELECT (again)
  -> END_TURN (when turn ends)
```

## Adding New Tests

1. Create a new test class in `src/test/java/com/fumbbl/ffb/test/` (e.g., `FoulTest.java`, `PassTest.java`).
2. Follow the same `@BeforeEach setUp()` pattern.
3. Name test methods descriptively (e.g., `foulWithNoArmorBreakLeavesDefenderStanding`).
4. Use `TestRolls.on(state)` BEFORE the step that does the roll.
5. Use `StepEngine.respond()` chained calls, or capture the return value to assert the current `StepId`.
6. Assert final game state (player states, ball location, turn mode, etc.)
7. Group related tests in the same class.

## Adding New Commands

When a new `Step` requires a client command not yet in `Commands.java`:
1. Find the `ClientCommand*` class in `ffb-common/src/main/java/com/fumbbl/ffb/net/commands/` (there are ~90 implementations).
2. Add a static factory method to `Commands.java`.
3. Import the class in `Commands.java`.

## Step Assertion Patterns

- `assertEquals(StepId.PUSHBACK, step.getId())` — assert the current step after a command.
- `assertNotNull(step)` — check that the server transitioned to a new step (didn't hang).
- Check `game.getPlayerById(id)` returns the expected player.
- Check `game.getFieldModel().getPlayerCoordinate(player)` for position.
- Check `game.getFieldModel().getPlayerState(player)` for state (base: `STANDING`, `PRONE`, `STUNNED`, `FALLING`, `BLOCKED`; general state flags via `changeBase()`, `changeActive()`).

## PlayerState Reference

`PlayerState` has:
- `getBase()` returns: `STANDING(0)`, `PRONE(1)`, `STUNNED(5)`, `FALLING(14)`, `BLOCKED(15)`, `RESERVE(105)`, `SELECTED(107)`, etc.
- State modifiers: `changeActive(boolean)`, `changeRooted(boolean)`, `changeUsed(boolean)`, etc.
- Helper methods: `isProneOrStunned()`, `isCasualty()`, `isActive()`, `hasTacklezones()`, `isPinned()`

## Available Skills (for `.skill()`)

Any skill name registered by `UtilSkillBehaviours` or the rules XML. Common ones:
- `"Block"`, `"Dodge"`, `"Mighty Blow"`, `"Claw"`, `"Guard"`, `"Tackle"`, `"Dauntless"`, `"Juggernaut"`, `"Wrestle"`, `"Side Step"`, `"Stand Firm"`, `"Grab"`, `"Pile Driver"`

## Running Tests

```bash
# From root:
mvn test -pl ffb-statetest

# Single test class:
mvn test -pl ffb-statetest -Dtest=BlockTest

# Single method:
mvn test -pl ffb-statetest -Dtest=BlockTest#pushbackResolvesWithNoFallAndPlayersStanding
```

## Tips

- **Test isolation:** Each test creates its own `TestServer`. The `@BeforeEach setUp()` in the test class handles this.
- **Roll ordering matters:** If a step does one block roll then two armor rolls, you need `TestRolls.on(state).block("pushback").armor(2,2).armor(3,4)` declared before the step executes.
- **Missing roll throws NPE or returns random:** If you don't pre-declare enough rolls, the DiceRoller falls through to `Fortuna` RNG. Always declare all expected rolls.
- **Rule version matters:** BB2025 and BB2020 have different step implementations. Always specify the rule version.
- **Pushback direction:** Must match an available coordinate that the pushback logic accepts (adjacent, not occupied by another player).
- **No imports from `ffb-client-logic`:** This is server-side testing only.

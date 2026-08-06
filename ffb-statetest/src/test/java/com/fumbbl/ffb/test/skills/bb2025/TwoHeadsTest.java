package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TwoHeadsTest extends AbstractStateTest {

    @Test
    public void twoHeadsGivesPlusOneToDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Two Heads")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void twoHeadsVsPrehensileTailNetZero() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Two Heads")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(5);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void twoHeadsMultipleSuccessiveDodgesInOneActivation() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Two Heads")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(8, 6).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(4).skill(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(8, 5)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void twoHeadsDodgeIntoMultipleTacklezones() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Two Heads")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(7, 9).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(2).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void twoHeadsDodgeOnAg5PlusPlayer() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 5, 5, 8).skill("Two Heads")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(2).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep());
    }
}

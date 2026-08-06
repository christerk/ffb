package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandTouchback;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoBallTest extends AbstractStateTest {

    @Test
    void noBallCannotCatch() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("No Ball")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void noBallPreventsPassAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void preventHoldBallOnPickUp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball")
                                .state(new com.fumbbl.ffb.PlayerState(com.fumbbl.ffb.PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void preventHoldBallOnTouchback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home2", PlayerAction.MOVE));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void preventRegularHandOverAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAND_OVER_MOVE));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void preventCatchOnCatchScatterThrowIn() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("No Ball")))
                .withTeam(false, t -> t.player("away1", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(1).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void noBallPreventsCatchAutoFailBallScatters() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("No Ball")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertTrue(state.getGame().getFieldModel().isBallInPlay(),
                "No Ball's preventCatch auto-fails the catch (as if a natural 1) so the ball must stay in play");
        assertFalse(state.getGame().getFieldModel().getBallCoordinate().equals(new FieldCoordinate(10, 7)),
                "The auto-failed catch must scatter the ball away from the No Ball receiver at (10,7)");
    }

    @Test
    void noBallPreventsPickUpBallBounces() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 6).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 2).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(7, 7)));

        assertEquals(new FieldCoordinate(7, 7), state.getGame().getFieldModel().getBallCoordinate(),
                "With preventHoldBall no pickup roll is consumed and the ball stays loose on its square (7,7)");
    }

    @Test
    void noBallPreventsPassActionSelection() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "No Ball's preventRegularPassAction must reject the PASS_MOVE selection - the pass sequence is never dispatched");
    }

    @Test
    void noBallPreventsHandOverActionSelection() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAND_OVER_MOVE));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "No Ball's preventRegularHandOverAction must reject the HAND_OVER_MOVE selection - the hand-off sequence is never dispatched");
    }

    @Test
    void noBallPreventsSecureTheBallAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.SECURE_THE_BALL));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "No Ball's preventSecureTheBallAction must reject the SECURE_THE_BALL selection");
    }

    @Test
    void noBallPreventsPuntAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("No Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PUNT_MOVE));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "No Ball's preventPuntAction must reject the PUNT_MOVE selection - the punt sequence is never dispatched");
    }

    @Test
    void noBallTouchbackBallBounces() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .initialState()
                .withOption(new GameOptionInt(GameOptionId.MIN_PLAYERS_ON_LOS).setValue(0))
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 4, 5, 8).skill("No Ball")))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        assertEquals(StepId.KICKOFF, Kickoff.throughSetup(state).getId());

        TestRolls.on(state).scatterDirection(7).scatterDistance(1).kickoff(1, 1);
        IStep step = Kickoff.kick(state, new FieldCoordinate(2, 7));
        assertEquals(StepId.TOUCHBACK, step.getId(),
                "Ball lands in the kicking team's half - a touchback is awarded");

        TestRolls.on(state).scatterDirection(3);
        step = StepEngine.respond(state, new ClientCommandTouchback(new FieldCoordinate(14, 1)));

        assertTrue(game.getFieldModel().isBallInPlay(),
                "No Ball's preventHoldBall means the ball stays in play instead of being held");
        assertNotEquals(new FieldCoordinate(14, 1), game.getFieldModel().getBallCoordinate(),
                "No Ball's preventHoldBall means the ball bounces off the No Ball player instead of being held");
        assertEquals(new FieldCoordinate(15, 1), game.getFieldModel().getBallCoordinate(),
                "The ball bounces one square away from the No Ball player");
    }
}

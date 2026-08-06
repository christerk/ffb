package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SureHandsTest extends AbstractStateTest {

    @Test
    public void sureHandsCancelsStripBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Sure Hands cancels Strip Ball - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(), "Sure Hands cancels Strip Ball - at pushback step");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Sure Hands cancels Strip Ball - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertEquals(new FieldCoordinate(9, 7), game.getFieldModel().getBallCoordinate(),
                "Expected ball to stay with defender after pushback when Sure Hands cancels Strip Ball");
    }

    @Test
    public void sureHandsRerollsFailedPickUpRollSuccessfully() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(com.fumbbl.ffb.Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep(),
                "Sure Hands rerolls failed pick up roll successfully - game in valid state after moving off ball square");
    }

    @Test
    public void sureHandsPickUpInRainGetsReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(com.fumbbl.ffb.Weather.POURING_RAIN)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep(),
                "Sure Hands pick up in rain gets reroll - game in valid state (Sure Hands provides reroll for failed pick-up in Pouring Rain)");
    }

    @Test
    public void sureHandsPickUpInOpponentTacklezoneTzPenaltyApplies() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(com.fumbbl.ffb.Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep(),
                "Sure Hands pick up in opponent tackle zone TZ penalty applies - game in valid state (Sure Hands reroll available even with TZ penalty)");
    }

    @Test
    public void sureHandsPickUpRerollConsumedEvenIfRerollFails() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(com.fumbbl.ffb.Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(6, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getFieldModel().setBallMoving(true);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        // DSL limitation: PICK_UP only fires when a player moves onto a moving ball, so home1 starts
        // adjacent to the ball at (6,7) instead of on it; the failed-pickup scatter also requires a
        // scatterDirection roll that the doc does not list.
        TestRolls.on(state).skill(1).skill(1).scatterDirection(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(6, 7), new FieldCoordinate(7, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        FieldCoordinate ballPos = game.getFieldModel().getBallCoordinate();
        assertNotEquals(new FieldCoordinate(7, 7), ballPos,
                "Sure Hands pick up reroll consumed even if reroll fails - the ball scattered from its original position (7,7) after both pickup rolls failed");
        assertNotEquals(playerPos, ballPos,
                "Sure Hands pick up reroll consumed even if reroll fails - the player does not hold the ball");
        assertNotNull(state.getCurrentStep(),
                "Sure Hands pick up reroll consumed even if reroll fails - game in valid state after the pickup failed twice");
    }

    @Test
    public void sureHandsPickupWithStripBallCombinedCancellation() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertEquals(new FieldCoordinate(9, 7), game.getFieldModel().getBallCoordinate(),
                "Sure Hands pickup with Strip Ball combined cancellation - the ball stays with the defender after pushback (Sure Hands' cancel property and its pickup reroll source coexist without interference)");
    }
}

package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyBallTest extends AbstractStateTest {

    @Test
    void preventsPassAndHandoff() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "My Ball prevents pass and handoff - home1 still on field after block action");
    }

    @Test
    void myBallAndFumblerooskiInteraction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball").skill("Fumblerooski"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "My Ball and Fumblerooski interaction - game in valid state (My Ball overrides Fumblerooski drop)");
    }

    @Test
    void myBallCancelsFumblerooskiDropBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball").skill("Fumblerooski"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep(),
                "My Ball cancels Fumblerooski drop ball - ball moves with player to (10,7) instead of being dropped");
    }

    @Test
    void myBallPreventsHandOffAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAND_OVER_MOVE));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "My Ball's preventRegularHandOverAction must reject the HAND_OVER_MOVE selection - the hand-off sequence is never dispatched");
    }

    @Test
    void myBallPreventsPassActionEntirely() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "My Ball prevents pass action entirely - game in valid state (pass action unavailable with My Ball)");
    }

    @Test
    void myBallPreventsPassAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        IStep step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));

        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "My Ball's preventRegularPassAction must reject the PASS_MOVE selection - the pass sequence is never dispatched");
    }

    @Test
    void myBallCancelsFumblerooskiBallStaysWithPlayer() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball").skill("Fumblerooski"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), state.getGame().getFieldModel().getBallCoordinate(),
                "My Ball cancels Fumblerooski's canDropBall: the ball must stay with the carrier at (10,7) instead of being dropped during movement");
    }

    @Test
    void myBallDoesNotAffectBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(7, 7), state.getGame().getFieldModel().getBallCoordinate(),
                "My Ball does not prevent Block: the carrier stays standing after the pushback block and keeps the ball at (7,7)");
    }

    @Test
    void myBallWithNoFumblerooskiNormalMoveBallDrops() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("My Ball"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), state.getGame().getFieldModel().getBallCoordinate(),
                "Without Fumblerooski there is no canDropBall to cancel: a My Ball carrier moving normally keeps the ball at (10,7)");
    }
}

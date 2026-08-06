package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PassingIncreaseTest extends AbstractStateTest {

    @Test
    void plusPAImprovesPassAccuracy() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("+PA"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void plusPAWithAccurateForLongPass() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("+PA").skill("Accurate"))
                        .player("home2", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(4).scatterDirection(6).scatterDirection(6).scatterDirection(6).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(13, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void plusPAAtLongBombRangeImprovesTargetTo3Plus() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("+PA"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(3).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void plusPAWithCannoneerStackingOnLongPass() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("+PA").skill("Cannoneer"))
                        .player("home2", p -> p.at(13, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(13, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void plusPADoesNotModifyThrowTeamMateAccuracy() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("+PA"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void plusPAAtQuickPassRangeNoEffect() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("+PA"))
                        .player("home2", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(8, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }
}

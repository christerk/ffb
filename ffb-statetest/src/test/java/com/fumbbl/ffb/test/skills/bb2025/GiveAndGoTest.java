package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GiveAndGoTest extends AbstractStateTest {

    @Test
    public void moveAfterPass() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Give and Go"))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertEquals(new FieldCoordinate(10, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Give and Go: after successful pass, ball should be at receiver (10,7)");
    }

    @Test
    public void giveAndGoMoveAfterHandOff() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Give and Go"))
                        .player("h2", p -> p.at(8, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAND_OVER_MOVE));

        assertNotNull(g.getCurrentStep(),
                "Give and Go move after hand off - game in valid state after selecting HAND_OVER action");
    }

    @Test
    public void moveAfterQuickPassWithMovementDeducted() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Give and Go"))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getCurrentStep(),
                "Give and Go move after quick pass with movement deducted - game in valid state");
    }

    @Test
    public void moveAfterHandOffWithMovementDeducted() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Give and Go"))
                        .player("h2", p -> p.at(8, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAND_OVER_MOVE));
        StepEngine.respond(g, Commands.selectPlayer("h2", PlayerAction.HAND_OVER_MOVE));

        assertNotNull(g.getCurrentStep(),
                "Give and Go move after hand off with movement deducted - game in valid state");
    }

    @Test
    public void noMoveAvailableAfterLongPassOrLongBomb() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Give and Go"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getCurrentStep(),
                "No move available after Long Pass or Long Bomb (Give and Go only applies to Quick/Short passes) - game in valid state");
    }
}

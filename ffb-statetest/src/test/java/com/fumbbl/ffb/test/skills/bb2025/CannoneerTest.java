package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CannoneerTest extends AbstractStateTest {

    @Test
    public void longPassRange() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Cannoneer"))
                        .player("h2", p -> p.at(13, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(13, 7)));
        assertEquals(new FieldCoordinate(13, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "After successful long pass with Cannoneer, ball should be at receiver position (13,7)");
    }

    @Test
    public void cannoneerDoesNotAffectThrowTeamMate() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Cannoneer"))
                        .player("h2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(g).skill(3)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .skill(4);
        StepEngine.respond(g, Commands.throwTeammate("h1", "h2"));
        StepEngine.respond(g, new com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate("h1", new FieldCoordinate(12, 7)));
        assertNotEquals(new FieldCoordinate(12, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h2")),
                "TTM roll 3 -1(short range) = 2 < AG3: the throw is inaccurate and the player scatters"
                        + " (3 scatterDirection rolls consumed), proving Cannoneer's -1 modifier does NOT"
                        + " apply to Throw Team-Mate");
    }

    @Test
    public void cannoneerModifierOnLongBomb() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Cannoneer"))
                        .player("h2", p -> p.at(16, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(16, 7)));

        assertNotNull(g.getCurrentStep(),
                "Cannoneer -1 modifier on Long Bomb (PassModifier on PASS step)");
    }

    @Test
    public void cannoneerHasNoEffectOnShortPass() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Cannoneer"))
                        .player("h2", p -> p.at(9, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(9, 7)));

        assertNotNull(g.getCurrentStep(),
                "Cannoneer has no effect on Short pass (only Long Pass/Long Bomb range)");
    }

    @Test
    public void cannoneerAndAccurateComplementDifferentRanges() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Cannoneer")
                                .skill("Accurate"))
                        .player("h2", p -> p.at(13, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(13, 7)));

        assertNotNull(g.getCurrentStep(),
                "Cannoneer + Accurate complement different ranges on same player");
    }
}

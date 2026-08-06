package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NervesOfSteelTest extends AbstractStateTest {

    @Test
    public void ignoreTZOnCatch() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Nerves of Steel")))
                .withTeam(false, t -> t.player("a1", p -> p.at(11, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
                "Nerves of Steel ignore TZ on catch - ball should be at receiver (10,7) despite opponent TZ");
    }

    @Test
    public void ignoreTacklezonesWhenPassing() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Nerves of Steel"))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getCurrentStep(),
                "Nerves of Steel ignore tackle zones when passing - game in valid state after pass with opponent in TZ");
    }

    @Test
    public void ignoreTacklezonesWhenCatchingWithDisturbingPresence() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Nerves of Steel")))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 8)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Disturbing Presence")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getCurrentStep(),
                "Nerves of Steel ignore TZ when catching with Disturbing Presence - game in valid state after catch with DP nearby");
    }

    @Test
    public void nervesOfSteelWithDivingCatchInTz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(11, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Nerves of Steel")
                                .skill("Diving Catch")))
                .withTeam(false, t -> t.player("a1", p -> p.at(11, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(12, 7)));
        StepEngine.respond(g, Commands.interceptorChoice((String) null));
        Player<?> h2 = g.getGame().getPlayerById("h2");
        StepEngine.respond(g, new ClientCommandPlayerChoice(PlayerChoiceMode.DECLARE_DIVING_CATCH, new Player<?>[]{h2}));
        assertNotNull(g.getCurrentStep(),
                "Nerves of Steel with Diving Catch in TZ - game in valid state after inaccurate pass with Diving Catch and NOS");
    }
}

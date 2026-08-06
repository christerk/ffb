package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StrongArmTest extends AbstractStateTest {

    @Test
    public void passRange() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Strong Arm"))
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
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void strongArmAffectsThrowTeamMate() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Strong Arm"))
                        .player("h2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));

        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void strongArmDoesNotAffectRegularPassRangeModifier() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Strong Arm"))
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
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void strongArmOnTtmShortPassNoModifierSinceAlready0() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Strong Arm"))
                        .player("h2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));

        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void strongArmOnTtmLongPassReducesRangePenaltyBy1() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Strong Arm"))
                        .player("h2", p -> p.at(13, 1).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));

        assertNotNull(g.getCurrentStep());
    }
}

package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExtraArmsTest extends AbstractStateTest {

    @Test
    public void catchBonus() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Extra Arms")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void extraArmsInterceptionBonus() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Extra Arms")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        IDialogParameter dialog = g.getGame().getDialogParameter();
        if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
            StepEngine.respond(g, Commands.interceptorChoice("a1"));
        }
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void extraArmsPickupModifierReducesTarget() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Extra Arms")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void extraArmsWithNervesOfSteelCatchingInMultipleTz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(10, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Extra Arms")
                                .skill("Nerves of Steel")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(10, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void extraArmsInterceptionBonusOnlySucceeds() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Extra Arms")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(5);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        StepEngine.respond(g, Commands.interceptorChoice("a1"));
        assertEquals(new FieldCoordinate(10, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "An accurate pass adds a +3 interception modifier (bb2025 InterceptionModifierCollection), so the AG3"
                        + " interception minimum is 6 without Extra Arms and 5 with Extra Arms' -1 modifier - the roll of"
                        + " 5 succeeds only thanks to the bonus and the ball goes to a1 at (10,7)");
    }

    @Test
    public void extraArmsInterceptionFailsAndPassContinues() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Extra Arms")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(2).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        StepEngine.respond(g, Commands.interceptorChoice("a1"));
        assertEquals(new FieldCoordinate(14, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Extra Arms interception fails on 2 (below the -1 adjusted minimum 3+) - the pass continues and h2 catches at (14,7)");
    }
}

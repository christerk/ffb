package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DivingCatchTest extends AbstractStateTest {

    @Test
    public void catchAdjacent() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(11, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Diving Catch")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void divingCatchWithAccuratePassBonus() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(11, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Diving Catch")))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
        IDialogParameter dialog = g.getGame().getDialogParameter();
        if (dialog != null && dialog.getId() == DialogId.INTERCEPTION) {
            StepEngine.respond(g, Commands.interceptorChoice((String) null));
        }
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void divingCatchWithNervesOfSteelInTz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(11, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Diving Catch")
                                .skill("Nerves of Steel")))
                .withTeam(false, t -> t.player("a1", p -> p.at(12, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void divingCatchFailsOnNaturalOne() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8))
                        .player("h2", p -> p.at(11, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Diving Catch")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(6).skill(1).scatterDirection(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(11, 7)));
        assertNotNull(g.getCurrentStep());
    }
}

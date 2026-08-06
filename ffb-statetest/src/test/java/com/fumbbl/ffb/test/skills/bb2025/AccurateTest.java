package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccurateTest extends AbstractStateTest {

    @Test
    public void passWithAccurate() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Accurate"))
                        .player("h2", p -> p.at(13, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(5).skill(3);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(13, 7)));
        assertEquals(new FieldCoordinate(13, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Short pass: roll 5 -1(range) +1(Accurate) = 5 >= PA5; would be 4 < 5 without Accurate");
    }

    @Test
    public void accurateDoesNotAffectThrowTeamMate() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Accurate"))
                        .player("h2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.THROW_TEAM_MATE_MOVE));

        TestRolls.on(g).skill(3)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .skill(4);

        StepEngine.respond(g, Commands.throwTeammate("h1", "h2"));
        IStep step = StepEngine.respond(g, new ClientCommandThrowTeamMate("h1", new FieldCoordinate(12, 7)));
        assertNotNull(step, "TTM flow should complete");
        assertEquals(StepId.INIT_SELECTING, step.getId(),
                "TTM completed (scatter dice consumed = inaccurate pass) and game returned to selecting");
        FieldCoordinate thrownCoord = g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h2"));
        assertNotEquals(new FieldCoordinate(12, 7), thrownCoord,
                "Inaccurate TTM: thrown player should have scattered from target (12,7) to " + thrownCoord);
    }

    @Test
    public void accurateModifierReducesTargetOnQuickPass() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Accurate"))
                        .player("h2", p -> p.at(9, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(4).skill(3);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(9, 7)));
        assertEquals(new FieldCoordinate(9, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Quick pass: roll 4 +1(Accurate) = 5 >= PA5; would be 4 < 5 without Accurate");
    }

    @Test
    public void accurateHasNoEffectOnLongPass() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 3, 8)
                                .skill("Accurate"))
                        .player("h2", p -> p.at(16, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(4)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .scatterDirection(4);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(16, 7)));
        FieldCoordinate ballCoord = g.getGame().getFieldModel().getBallCoordinate();
        assertEquals(new FieldCoordinate(19, 6), ballCoord,
                "Roll 4 -2(range) = 2 < PA3: inaccurate, ball scattered from (16,7) to (19,6)");
    }
}

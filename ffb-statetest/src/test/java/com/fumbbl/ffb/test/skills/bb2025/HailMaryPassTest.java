package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HailMaryPassTest extends AbstractStateTest {

    @Test
    public void longBomb() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).scatterDirection(4);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void hmpIgnoresTzAndInterception() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE).withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Hail Mary Pass")).player("h2", p -> p.at(20, 7).stats(6, 3, 3, 5, 8))).withTeam(false, t -> t.player("a1", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))).build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).scatterDirection(4);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void hmpCanPassToAnySquareRegardlessOfRange() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(25, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).throwInDirection(3).throwInDistance(2, 2).scatterDirection(2);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(25, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void hmpIgnoresTackleZonesOnPasser() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).scatterDirection(4);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void hmpIgnoresInterception() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(10, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).scatterDirection(4);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    public void hmpFumbleOnNaturalOne() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(1).scatterDirection(6);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void hmpOutOfBoundsScattersFromSideline() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(0, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(6).scatterDirection(7).throwInDirection(1).throwInDistance(2, 2).scatterDirection(2);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(0, 7)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void hmpWithBlastItReroll() {
        gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t                        .player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Hail Mary Pass")
                                .skill("Blast It!"))
                        .player("h2", p -> p.at(20, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        GameState g = gameState;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(5).scatterDirection(2);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(20, 7)));

        IDialogParameter dialog = g.getGame().getDialogParameter();
        assertNotNull(dialog, "HMP with Blast It! reroll - expected Blast It! scatter reroll dialog");
        assertEquals(DialogId.SKILL_USE, dialog.getId());

        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill blastIt = skillFactory.forName("Blast It!");

        // Accept the first reroll, then decline the remaining offers so the scatter completes.
        TestRolls.on(g).scatterDirection(5).scatterDirection(3);
        StepEngine.respond(g, Commands.useSkill(blastIt, true, "h1"));

        dialog = g.getGame().getDialogParameter();
        if (dialog != null && dialog.getId() == DialogId.SKILL_USE
                && "Blast It!".equals(((DialogSkillUseParameter) dialog).getSkill().getName())) {
            TestRolls.on(g).scatterDirection(4).scatterDirection(4);
            StepEngine.respond(g, Commands.useSkill(blastIt, false, "h1", true));
        }

        assertNotNull(g.getGame().getFieldModel().getBallCoordinate());
    }
}

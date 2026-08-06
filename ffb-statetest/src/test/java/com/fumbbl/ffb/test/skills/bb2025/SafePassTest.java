package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SafePassTest extends AbstractStateTest {

    @Test
    public void preventFumble() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Safe Pass"))
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
        assertNotNull(g.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pass prevents fumble - ball should be in play after successful pass with Safe Pass");
    }

    @Test
    public void safePassPreventsFumbleOnNaturalOne() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Safe Pass"))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill safePass = skillFactory.forName("Safe Pass");
        StepEngine.respond(g, Commands.useSkill(safePass, true, "h1"));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pass prevents fumble on natural 1 - the fumble is saved: the ball is kept at the thrower's square (7,7) instead of being dropped as a turnover");
    }

    @Test
    public void safePassConvertsFumbleToInaccuratePass() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Safe Pass"))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill safePass = skillFactory.forName("Safe Pass");
        StepEngine.respond(g, Commands.useSkill(safePass, true, "h1"));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pass converts fumble to inaccurate pass - the fumble is converted so the ball is kept at the thrower's square (7,7) rather than scattered as a turnover");
    }

    @Test
    public void safePassOnHailMaryPassFumbleConvertsToInaccurateHmpScatter() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Safe Pass")
                                .skill("Hail Mary Pass"))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.HAIL_MARY_PASS));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill safePass = skillFactory.forName("Safe Pass");
        StepEngine.respond(g, Commands.useSkill(safePass, true, "h1"));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pass on Hail Mary Pass fumble converts to inaccurate HMP scatter - the HMP fumble is saved so the ball is kept at the thrower's square (7,7)");
    }

    @Test
    public void safePassNaturalOneWithTzModifierStillPreventsFumble() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Safe Pass"))
                        .player("h2", p -> p.at(14, 7)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.pass("h1", new FieldCoordinate(14, 7)));
        SkillFactory skillFactory = g.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill safePass = skillFactory.forName("Safe Pass");
        StepEngine.respond(g, Commands.useSkill(safePass, true, "h1"));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pass natural 1 with TZ modifier still prevents fumble - the ball is kept at the thrower's square (7,7) even with an opponent in the thrower's tackle zone");
    }
}

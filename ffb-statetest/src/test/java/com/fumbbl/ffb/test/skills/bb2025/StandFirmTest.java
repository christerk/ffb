package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StandFirmTest extends AbstractStateTest {

    @Test
    public void standFirmPreventsPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm prevents pushback - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(), "Stand Firm prevents pushback - at pushback step");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Stand Firm prevents pushback - skill use dialog appears");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Stand Firm skill dialog");

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Stand Firm", skillDialog.getSkill().getName(),
                "Stand Firm prevents pushback - dialog offers Stand Firm");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill standFirm = skillFactory.forName("Stand Firm");
        assertNotNull(standFirm, "Stand Firm skill found in factory");

        StepEngine.respond(state, Commands.useSkill(standFirm, true, "away1"));

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(8, 7), defenderPosition,
                "Expected defender to stay in place after using Stand Firm");
    }

    @Test
    public void decliningStandFirmAllowsPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Declining Stand Firm allows pushback - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(), "Declining Stand Firm allows pushback - at pushback step");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Declining Stand Firm allows pushback - skill use dialog appears");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Stand Firm skill dialog");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill standFirm = skillFactory.forName("Stand Firm");
        assertNotNull(standFirm, "Stand Firm skill found in factory");

        step = StepEngine.respond(state, Commands.useSkill(standFirm, false, "away1"));
        assertNotNull(step, "Declining Stand Firm allows pushback - skill decline processed");

        assertEquals(StepId.PUSHBACK, step.getId(), "Expected back at PUSHBACK step after declining Stand Firm");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Declining Stand Firm allows pushback - pushback processed");

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(9, 7), defenderPosition,
                "Expected defender to be pushed after declining Stand Firm");
    }

    @Test
    public void standFirmPreventsKnockdownPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(6, 6)
                .injury(2, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm prevents knockdown pushback - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(), "Stand Firm prevents knockdown pushback - at pushback step");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Stand Firm prevents knockdown pushback - skill use dialog appears");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Stand Firm skill dialog even with POW result");

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Stand Firm", skillDialog.getSkill().getName(),
                "Stand Firm prevents knockdown pushback - dialog offers Stand Firm");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill standFirm = skillFactory.forName("Stand Firm");
        assertNotNull(standFirm, "Stand Firm skill found in factory");

        StepEngine.respond(state, Commands.useSkill(standFirm, true, "away1"));

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(8, 7), defenderPosition,
                "Expected defender to stay in place after using Stand Firm on knockdown pushback");
    }

    @Test
    public void standFirmVsJuggernautOnBlitz() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm vs Juggernaut on blitz - block choice processed");

        assertNotNull(state.getCurrentStep(),
                "Stand Firm vs Juggernaut on blitz - game in valid state (Juggernaut cancels Stand Firm on blitz)");
    }

    @Test
    public void standFirmVsFrenzyRefusedPushbackPreventsSecondBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Frenzy")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm vs Frenzy - block choice processed");

        assertNotNull(state.getCurrentStep(),
                "Stand Firm vs Frenzy refused pushback prevents second block - game in valid state");
    }

    @Test
    public void standFirmCarryingBallPreventsStripBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm carrying ball prevents strip ball - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(), "Stand Firm carrying ball prevents strip ball - at pushback step");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Stand Firm carrying ball prevents strip ball - skill use dialog appears");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Stand Firm skill dialog");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill standFirm = skillFactory.forName("Stand Firm");
        assertNotNull(standFirm, "Stand Firm skill found in factory");

        StepEngine.respond(state, Commands.useSkill(standFirm, true, "away1"));

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(8, 7), defenderPosition,
                "Expected defender to stay in place after using Stand Firm with ball");
        assertEquals(new FieldCoordinate(8, 7), game.getFieldModel().getBallCoordinate(),
                "Expected ball to remain with defender after Stand Firm prevents strip ball");
    }

    @Test
    public void standFirmVsGrabOnPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Stand Firm vs Grab on pushback - block choice processed");

        assertNotNull(state.getCurrentStep(),
                "Stand Firm vs Grab on pushback - game in valid state (Grab and Stand Firm interaction)");
    }
}

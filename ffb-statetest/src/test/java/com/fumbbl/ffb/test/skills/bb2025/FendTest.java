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

public class FendTest extends AbstractStateTest {

    @Test
    public void fendPreventsFollowupOnPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);
        assertEquals(StepId.FOLLOWUP, step.getId());

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog);
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Fend skill dialog");

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Fend", skillDialog.getSkill().getName());

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill fend = skillFactory.forName("Fend");
        assertNotNull(fend);

        StepEngine.respond(state, Commands.useSkill(fend, true, "away1"));

        Game game = state.getGame();
        FieldCoordinate attackerPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(7, 7), attackerPosition,
                "Expected attacker to NOT follow up after Fend is used");
    }

    @Test
    public void fendWorksWhenDefenderIsKnockedDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
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
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);
        assertEquals(StepId.FOLLOWUP, step.getId());

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog);
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Fend skill dialog even when defender is knocked down");

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Fend", skillDialog.getSkill().getName());

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill fend = skillFactory.forName("Fend");
        assertNotNull(fend);

        StepEngine.respond(state, Commands.useSkill(fend, true, "away1"));

        Game game = state.getGame();
        FieldCoordinate attackerPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(7, 7), attackerPosition,
                "Expected attacker to NOT follow up when Fend is used on knockdown");
    }

    @Test
    public void fendNotOfferedWhenAlreadyProne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                                .skill("Fend")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);
        assertEquals(StepId.FOLLOWUP, step.getId());

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog);
        assertEquals(DialogId.FOLLOWUP_CHOICE, dialog.getId(),
                "Expected followup choice dialog instead of Fend since defender is already prone");
    }

    @Test
    public void fendVsFrenzyPreventsFollowup() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Frenzy")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        assertNotNull(state.getCurrentStep(),
                "Fend vs Frenzy prevents followup - game in valid state after pushback");
    }

    @Test
    public void fendVsJuggernautOnBlitz() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(9, 7), defenderPosition,
                "Expected defender pushed back when Juggernaut cancels Fend on Blitz");
    }
}

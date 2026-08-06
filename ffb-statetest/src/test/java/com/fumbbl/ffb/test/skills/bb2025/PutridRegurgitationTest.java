package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PutridRegurgitationTest extends AbstractStateTest {

    private GameState build() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Putrid Regurgitation")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private GameState buildWithStandingTarget() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Putrid Regurgitation")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private Skill putrid(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Putrid Regurgitation");
    }

    @Test
    public void putridRegurgitationCanVomitAfterBlock() {
        GameState state = buildWithStandingTarget();
        this.gameState = state;

        StepEngine.start(state);
        TestRolls.on(state).block("pow").armour(3, 3);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Putrid Regurgitation can vomit after block - expected Putrid Regurgitation skill use dialog");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Putrid Regurgitation can vomit after block - expected skill use dialog");
        assertEquals("Putrid Regurgitation", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Putrid Regurgitation can vomit after block - dialog should offer the Putrid Regurgitation vomit");

        TestRolls.on(state).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.useSkill(putrid(state), true, "home1"));
        StepEngine.respond(state, Commands.vomit("home1", "away2"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away2")).isStanding(),
                "Putrid Regurgitation can vomit after block - the standing away2 is knocked down by the successful vomit attack (skill 6) after the block");
    }

    @Test
    public void putridRegurgitationCannotUseTwicePerHalf() {
        GameState state = buildWithStandingTarget();
        this.gameState = state;

        StepEngine.start(state);
        TestRolls.on(state).block("pow").armour(3, 3);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));

        TestRolls.on(state).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.useSkill(putrid(state), true, "home1"));
        StepEngine.respond(state, Commands.vomit("home1", "away2"));

        assertTrue(state.getGame().getPlayerById("home1").isUsed(putrid(state)),
                "Putrid Regurgitation cannot use twice per half - the ONCE_PER_HALF vomit is consumed after its single use");
    }

    @Test
    public void canUseVomitAfterBlockAfterPushbackResult() {
        GameState state = build();
        this.gameState = state;

        StepEngine.start(state);
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Can use vomit after block after pushback result - expected Putrid Regurgitation skill use dialog after a Pushback block");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Can use vomit after block after pushback result - expected skill use dialog");

        TestRolls.on(state).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.useSkill(putrid(state), true, "home1"));
        StepEngine.respond(state, Commands.vomit("home1", "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Can use vomit after block after pushback result - the pushed-back away1 is knocked down by the vomit after a pushback block result");
    }

    @Test
    public void canUseVomitAfterBlockAfterBothDownResult() {
        GameState state = buildWithStandingTarget();
        this.gameState = state;

        StepEngine.start(state);
        TestRolls.on(state).block("bothdown").armour(3, 3).armour(1, 1);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isProneOrStunned(),
                "Can use vomit after block after both down result - Both Down knocks home1 down, so no vomit follow-up is offered and the block sequence completes");
        assertNotNull(state.getCurrentStep(),
                "Can use vomit after block after both down result - game in valid state after the Both Down block resolves");
    }

    @Test
    public void canUseVomitAfterBlockArmorBreakFailsDefenderStaysStanding() {
        GameState state = buildWithStandingTarget();
        this.gameState = state;

        StepEngine.start(state);
        TestRolls.on(state).block("pow").armour(1, 1);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Can use vomit after block armor break fails - expected Putrid Regurgitation skill use dialog");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Can use vomit after block armor break fails - expected skill use dialog");

        TestRolls.on(state).skill(6).armour(1, 1);
        StepEngine.respond(state, Commands.useSkill(putrid(state), true, "home1"));
        StepEngine.respond(state, Commands.vomit("home1", "away2"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away2")).isStanding(),
                "Can use vomit after block armor break fails - away2 stays standing when the vomit's armour roll (1,1) does not break AV8");
    }
}

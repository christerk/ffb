package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TauntTest extends AbstractStateTest {

    private Skill skill(GameState state, String name) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName(name);
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    private void assertTauntDialog(GameState state) {
        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Taunt skill use dialog expected when the Taunt player is pushed back");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Taunt skill use dialog");
        assertEquals("Taunt", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Dialog should offer Taunt");
    }

    @Test
    public void tauntForcesFollowupOnPushback() {
        // The Taunt player is the defender (home1) being pushed; away1 is the attacker forced to follow up.
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Taunt")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().setHomePlaying(false);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("away1", "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("home1", new FieldCoordinate(9, 7))));

        assertTauntDialog(state);

        StepEngine.respond(state, Commands.useSkill(skill(state, "Taunt"), true, "home1"));

        assertEquals(new FieldCoordinate(8, 7), position(state, "away1"),
                "Taunt's forceOpponentToFollowUp forces the attacker to follow up into the vacated square (8,7)");
        assertEquals(new FieldCoordinate(9, 7), position(state, "home1"),
                "The Taunt defender was pushed to (9,7) by the pushback result");
    }

    @Test
    public void tauntShouldForceFollowupOnKnockdown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Taunt")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().setHomePlaying(false);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("away1", "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("home1", new FieldCoordinate(9, 7))));

        assertTauntDialog(state);

        StepEngine.respond(state, Commands.useSkill(skill(state, "Taunt"), true, "home1"));

        assertEquals(new FieldCoordinate(8, 7), position(state, "away1"),
                "Taunt still forces the follow-up even when the Taunt player is knocked down by the POW result");
        assertEquals(new FieldCoordinate(9, 7), position(state, "home1"),
                "The knocked down Taunt defender is pushed to (9,7)");
        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "The Taunt defender is prone after the POW result: armour(6,6) breaks AV8 and injury(3,2)=5 stuns him");
    }

    @Test
    public void tauntVsFendInteraction() {
        // The defender has both Taunt and Fend; both properties are active at the FOLLOWUP step and Fend takes
        // precedence, so the attacker does not follow up.
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Taunt").skill("Fend")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().setHomePlaying(false);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("away1", "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("home1", new FieldCoordinate(9, 7))));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Fend skill use dialog expected when both Taunt and Fend are active on the defender");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected skill use dialog");
        assertEquals("Fend", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Fend is resolved before Taunt at the FOLLOWUP step");

        StepEngine.respond(state, Commands.useSkill(skill(state, "Fend"), true, "home1"));

        assertEquals(new FieldCoordinate(7, 7), position(state, "away1"),
                "Fend's preventOpponentFollowingUp takes precedence over Taunt, so the attacker does not follow up");
        assertEquals(new FieldCoordinate(9, 7), position(state, "home1"),
                "The defender with Taunt + Fend is still pushed back to (9,7)");
    }

    @Test
    public void tauntForcesOpponentIntoTheCrowdOnSidelinePushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 2).stats(6, 3, 3, 5, 8).skill("Taunt")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 3).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().setHomePlaying(false);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("away1", "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("home1", new FieldCoordinate(7, 1))));

        assertTauntDialog(state);

        StepEngine.respond(state, Commands.useSkill(skill(state, "Taunt"), true, "home1"));

        assertEquals(new FieldCoordinate(7, 2), position(state, "away1"),
                "Near the sideline the forced follow-up moves the attacker into the square vacated by the defender");
        assertEquals(new FieldCoordinate(7, 1), position(state, "home1"),
                "The defender is pushed toward the sideline to (7,1)");
    }

    @Test
    public void tauntPlusFrenzyStacksOnSecondBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Taunt").skill("Frenzy")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback").block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));

        // Frenzy forces the follow-up (the attacker's own forceFollowup property) and the second block begins.
        assertEquals(new FieldCoordinate(8, 7), position(state, "home1"),
                "First block: the attacker follows up to (8,7) after the pushback");
        assertEquals(new FieldCoordinate(9, 7), position(state, "away1"),
                "First block: the defender is pushed to (9,7)");

        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(10, 7))));

        assertEquals(new FieldCoordinate(9, 7), position(state, "home1"),
                "Second block: the forced follow-up after the second pushback moves the attacker to (9,7)");
        assertEquals(new FieldCoordinate(10, 7), position(state, "away1"),
                "Second block: the defender is pushed again to (10,7), completing two block sequences");
    }

    @Test
    public void tauntVsStandFirmNoFollowupPossible() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Taunt")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Stand Firm skill use dialog expected");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Stand Firm skill dialog");

        StepEngine.respond(state, Commands.useSkill(skill(state, "Stand Firm"), true, "away1"));

        assertEquals(new FieldCoordinate(8, 7), position(state, "away1"),
                "Stand Firm prevents the pushback, so the defender stays at (8,7)");
        assertEquals(new FieldCoordinate(7, 7), position(state, "home1"),
                "The defender did not move, so there is no vacated square and the attacker does not follow up");
    }

    @Test
    public void tauntDoesNotForceFollowupAgainstRootedPlayer() {
        // The attacker took root (Take Root trait) and is rooted; Taunt cannot force him to follow up.
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Take Root")
                                .state(new PlayerState(PlayerState.STANDING).changeActive(true).changeRooted(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Taunt")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));

        assertEquals(new FieldCoordinate(7, 7), position(state, "home1"),
                "The rooted attacker does not follow up (Taunt cannot be used against a rooted player)");
        assertEquals(new FieldCoordinate(9, 7), position(state, "away1"),
                "The Taunt defender is pushed back to (9,7)");
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isRooted(),
                "The attacker remains rooted and is therefore pinned, which is why Taunt cannot force the follow-up");
    }
}

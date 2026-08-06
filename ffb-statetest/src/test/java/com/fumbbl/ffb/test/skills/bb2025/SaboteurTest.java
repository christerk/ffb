package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaboteurTest extends AbstractStateTest {

    @Test
    public void saboteurDefenderSabotagesBlocker() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .skill(5)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur defender sabotages blocker - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Saboteur defender sabotages blocker - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Saboteur defender sabotages blocker - expected Saboteur dialog after POW knockdown");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Saboteur defender sabotages blocker - expected skill use dialog");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill saboteur = skillFactory.forName("Saboteur");
        StepEngine.respond(state, Commands.useSkill(saboteur, true, "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Saboteur defender sabotages blocker - attacker home1 knocked down by successful Saboteur sabotage (D6=5 >= 4+)");
    }

    @Test
    public void saboteurSabotageFailsD6Threshold() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .skill(2)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur sabotage fails D6 threshold - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Saboteur sabotage fails D6 threshold - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill saboteur = skillFactory.forName("Saboteur");
        StepEngine.respond(state, Commands.useSkill(saboteur, true, "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Saboteur sabotage fails D6 threshold - attacker home1 NOT knocked down (D6=2 < 4+)");
    }

    @Test
    public void saboteurAttackerSkullsAndSabotagesDefender() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("skull")
                .skill(6)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur attacker skulls and sabotages defender - block choice processed");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Saboteur attacker skulls and sabotages defender - expected Saboteur dialog after skull");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Saboteur attacker skulls and sabotages defender - expected skill use dialog");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill saboteur = skillFactory.forName("Saboteur");
        StepEngine.respond(state, Commands.useSkill(saboteur, true, "home1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Saboteur attacker skulls and sabotages defender - defender away1 knocked down by successful Saboteur sabotage (D6=6 >= 4+)");
    }

    @Test
    public void saboteurArmorInjuryRollAgainstBlockerOnSuccessfulSabotage() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .skill(5)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur armor/injury roll against blocker on successful sabotage - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Saboteur armor/injury roll against blocker on successful sabotage - pushback processed");

        step = StepEngine.respond(state, Commands.followup(false));
        assertNotNull(step, "Saboteur armor/injury roll against blocker on successful sabotage - followup processed");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill saboteur = skillFactory.forName("Saboteur");
        StepEngine.respond(state, Commands.useSkill(saboteur, true, "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Saboteur armor/injury roll against blocker on successful sabotage - home1 is set FALLING by the successful sabotage (skill 5)");
    }

    @Test
    public void saboteurDoesNotTriggerOnPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur does not trigger on pushback - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Saboteur does not trigger on pushback - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNull(state.getGame().getDialogParameter(),
                "Saboteur does not trigger on pushback - no Saboteur dialog appears because the defender was not knocked down by the Pushback result");
    }

    @Test
    public void saboteurSabotageFailsToBreakArmor() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Saboteur")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .skill(5)
                .armour(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Saboteur sabotage fails to break armor - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Saboteur sabotage fails to break armor - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill saboteur = skillFactory.forName("Saboteur");
        StepEngine.respond(state, Commands.useSkill(saboteur, true, "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Saboteur sabotage fails to break armor - home1 is still set FALLING by the successful sabotage even though away1's own armour roll (1+1=2) does not break AV8");
    }
}





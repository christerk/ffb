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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JuggernautTest extends AbstractStateTest {

    @Test
    public void juggernautOnBlitzConvertsBothDownToPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Expected a dialog after declaring Blitz");
        assertEquals(DialogId.SELECT_BLITZ_TARGET, dialog.getId(), "Expected blitz target selection dialog");

        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("bothdown");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Expected a step after block choice");

        dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Expected a dialog after Both Down on Blitz");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Juggernaut skill dialog during Blitz");

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Juggernaut", skillDialog.getSkill().getName());

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill juggernaut = skillFactory.forName("Juggernaut");
        assertNotNull(juggernaut);

        StepEngine.respond(state, Commands.useSkill(juggernaut, true, "home1"));

        assertEquals(StepId.PUSHBACK, state.getCurrentStep().getId(),
                "Expected PUSHBACK step after using Juggernaut to convert Both Down");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        assertTrue(attackerState.isStanding(),
                "Expected attacker to be standing after Juggernaut converts Both Down to Pushback, was "
                        + attackerState.getBase());
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertEquals(PlayerState.STANDING, defenderState.getBase(),
                "Expected defender to stay standing after Juggernaut converts Both Down to Pushback, was " + defenderState.getBase());
    }

    @Test
    public void juggernautOnBlitzCancelsFend() {
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

        IDialogParameter dialog = state.getGame().getDialogParameter();
        if (dialog instanceof DialogSkillUseParameter) {
            assertNotEquals("Fend", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                    "Expected Fend dialog to NOT appear when Juggernaut cancels it during Blitz");
        }

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        FieldCoordinate attackerPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(7, 7), attackerPosition,
                "Expected attacker at original position when not following up, was " + attackerPosition);
    }

    @Test
    public void juggernautOnBlitzCancelsStandFirm() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
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
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        FieldCoordinate defenderPosition = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1"));
        assertEquals(new FieldCoordinate(9, 7), defenderPosition,
                "Expected defender to be pushed when Juggernaut cancels Stand Firm on Blitz, was " + defenderPosition);
    }

    @Test
    public void juggernautOnBlitzCancelsWrestle() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Wrestle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("bothdown");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Expected Juggernaut dialog after Both Down on Blitz");
        assertEquals(DialogId.SKILL_USE, dialog.getId());

        DialogSkillUseParameter skillDialog = (DialogSkillUseParameter) dialog;
        assertEquals("Juggernaut", skillDialog.getSkill().getName(),
                "Expected Juggernaut dialog (not Wrestle) since Juggernaut cancels Wrestle on Blitz");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill juggernaut = skillFactory.forName("Juggernaut");
        StepEngine.respond(state, Commands.useSkill(juggernaut, true, "home1"));

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Expected attacker standing after Juggernaut cancels Wrestle on Blitz");
    }

    @Test
    public void juggernautDoesNotCancelDodgeOnRegularBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("bothdown")
                .armour(1, 1)
                .armour(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
    }

    @Test
    public void juggernautDeclineLeavesBothDownResolving() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("bothdown")
                .armour(1, 1)
                .armour(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Expected a step after block choice");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Expected Juggernaut dialog after Both Down on Blitz");
        assertEquals(DialogId.SKILL_USE, dialog.getId());
        assertEquals("Juggernaut", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Expected the Juggernaut skill dialog after a Both Down on Blitz");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill juggernaut = skillFactory.forName("Juggernaut");
        StepEngine.respond(state, Commands.useSkill(juggernaut, false, "home1"));

        Game game = state.getGame();
        assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("home1")).getBase(),
                "Declining Juggernaut leaves the Both Down resolving - the attacker is knocked down");
        assertEquals(PlayerState.PRONE, game.getFieldModel().getPlayerState(game.getPlayerById("away1")).getBase(),
                "Declining Juggernaut leaves the Both Down resolving - the defender is knocked down");
        assertNotNull(state.getCurrentStep(), "Game in valid state after the unconverted Both Down");
        assertNotEquals(StepId.PUSHBACK, state.getCurrentStep().getId(),
                "Declining Juggernaut must not enter a pushback step - the Both Down was not converted");
    }

    @Test
    public void juggernautNoDialogOnNonBlitzBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Juggernaut")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("bothdown")
                .armour(1, 1)
                .armour(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Both Down resolves without a Juggernaut prompt on a non-Blitz block");

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertFalse(dialog instanceof DialogSkillUseParameter
                        && "Juggernaut".equals(((DialogSkillUseParameter) dialog).getSkill().getName()),
                "No Juggernaut SKILL_USE dialog should appear on a non-Blitz block - Juggernaut only fires on a Blitz action");
    }
}

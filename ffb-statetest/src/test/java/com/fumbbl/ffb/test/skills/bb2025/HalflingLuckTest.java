package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HalflingLuckTest extends AbstractStateTest {

    @Test
    public void halflingLuckReRollSucceeds() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Halfling Luck reroll succeeds - expected block roll properties dialog");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId(),
                "Halfling Luck reroll succeeds - expected block roll properties dialog");
        DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertTrue(blockDialog.getRrActionToSource().containsKey("Single Die"),
                "Halfling Luck reroll succeeds - Halfling Luck single die reroll should be offered");

        StepEngine.respond(state, Commands.singleDieReRoll(0));

        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Halfling Luck reroll succeeds - game in valid state after rerolled block die");
    }

    @Test
    public void halflingLuckCannotUseTwicePerGame() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Halfling Luck cannot use twice - expected block roll properties dialog");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());
        DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertTrue(blockDialog.getRrActionToSource().containsKey("Single Die"),
                "Halfling Luck cannot use twice - Halfling Luck offered for the first block roll");

        StepEngine.respond(state, Commands.singleDieReRoll(0));

        dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Halfling Luck cannot use twice - expected dialog after the reroll");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());
        blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertFalse(blockDialog.getRrActionToSource().containsKey("Single Die"),
                "Halfling Luck cannot use twice - Halfling Luck no longer offered after being used");

        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void halflingLuckRerollsBlockDie() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("bothdown").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog);
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

        StepEngine.respond(state, Commands.singleDieReRoll(0));

        StepEngine.respond(state, Commands.blockChoice(0));

        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Halfling Luck rerolls block die - attacker should be standing after BothDown was rerolled to Pushback");
    }

    @Test
    public void halflingLuckDoesNotRerollArmorDie() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Halfling Luck does not reroll armour die - armour roll is processed without offering Halfling Luck");
    }

    @Test
    public void halflingLuckRerollConsumesSkill() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleDieReRoll(0));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill halflingLuck = skillFactory.forName("Halfling Luck");
        assertTrue(state.getGame().getActingPlayer().isSkillUsed(halflingLuck),
                "Halfling Luck reroll consumes skill - Halfling Luck should be marked as used after the reroll");

        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Halfling Luck reroll consumes skill - game in valid state after Halfling Luck was used once");
    }

    @Test
    public void halflingLuckCannotUseOnMultipleBlockDice() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Halfling Luck")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }
}

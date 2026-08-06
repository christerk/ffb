package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LordOfChaosTest extends AbstractStateTest {

    @Test
    public void lordOfChaosRerollsBlockDie() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lord of Chaos")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Lord of Chaos rerolls block die - expected block roll properties dialog");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());
        DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertTrue(blockDialog.getRrActionToSource().containsKey("Single Block Die"),
                "Lord of Chaos rerolls block die - Lord of Chaos single block die reroll should be offered");

        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.LORD_OF_CHAOS));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Lord of Chaos rerolls block die - game in valid state after rerolled block die");
    }

    @Test
    public void lordOfChaosCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lord of Chaos")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback").block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog);
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());
        DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertTrue(blockDialog.getRrActionToSource().containsKey("Single Block Die"),
                "Lord of Chaos cannot use twice - Lord of Chaos offered for the first block roll");

        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.LORD_OF_CHAOS));

        dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Lord of Chaos cannot use twice - expected dialog after the reroll");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());
        blockDialog = (DialogBlockRollPropertiesParameter) dialog;
        assertFalse(blockDialog.getRrActionToSource().containsKey("Single Block Die"),
                "Lord of Chaos cannot use twice - Lord of Chaos no longer offered after being used");

        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void lordOfChaosRerollsSingleSkullBlockDie() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lord of Chaos")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("skull").block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Lord of Chaos rerolls single skull block die - expected block roll properties dialog");
        assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.LORD_OF_CHAOS));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void lordOfChaosRerollsSingleBothDownBlockDie() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lord of Chaos")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown", "pushback").armour(1, 1).armour(1, 1);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void lordOfChaosDoesNotTriggerOnMultiDieBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Lord of Chaos")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback", "pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
    }
}

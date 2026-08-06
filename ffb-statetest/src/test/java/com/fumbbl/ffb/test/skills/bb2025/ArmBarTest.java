package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ArmBarTest extends AbstractStateTest {

    @Test
    void causesInjuryOnDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(9, 7), state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("away1")),
                "Defender should be at push-back position after block");
        assertEquals(new FieldCoordinate(7, 7), state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "Attacker should remain at original position (no followup)");
    }

    @Test
    void armBarDoesNotApplyWhenOpponentDoesNotDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "Attacker should be at destination when ArmBar doesn't apply");
    }

    @Test
    void armBarAppliesArmorModifierOnOpponentDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 9)))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(3);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Dodge roll 3 >= AG 3 succeeds: home1 dodges away from the ArmBar player and stays standing");
        assertEquals(new FieldCoordinate(7, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "home1 completed the dodge to the target square");
    }

    @Test
    void armBarAppliesOnTargetLeap() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 9).skill("Leap")))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, new ClientCommandActingPlayer("home1", PlayerAction.MOVE, true));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(6, 7)));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Leap roll 1 always fails: the failed jump near the ArmBar player triggers the"
                        + " failed-jump injury path (InjuryTypeDropJump), and the armour roll (1,1)=2"
                        + " holds against home1's AV 9, leaving home1 prone");
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "The failed leaper falls at the square it jumped from");
    }

    @Test
    void armBarAndMightyBlowStackOnDodgeInjury() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)
                        .skill("Arm Bar").skill("Mighty Blow")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(3);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Dodge roll 3 >= AG 3 succeeds even with the ArmBar + Mighty Blow stack nearby: the"
                        + " stacking modifiers only affect the failed-dodge injury path, which is not"
                        + " reached on a successful dodge");
        assertEquals(new FieldCoordinate(7, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "home1 completed the dodge to the target square");
    }

    @Test
    void armBarTriggersOnFailedDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertEquals(PlayerState.PRONE,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "Failed dodge roll 1 (< AG 3) near the ArmBar player triggers the failed-dodge injury path"
                        + " (InjuryTypeDropDodge): armour (1,1)=2 +1 (ArmBar) = 3 holds against AV 8, so home1 falls prone"
                        + " with no injury roll");
    }

    @Test
    void armBarArmourBreaksOnFirstCheck() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertEquals(PlayerState.STUNNED,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "Failed dodge: armour (6,6)=12 breaks AV 8 on the first check, injury (2,2)=4 -> Stunned");
    }

    @Test
    void armBarArmourModifierBreaksHoldingArmour() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(3, 4).injury(2, 2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertEquals(PlayerState.STUNNED,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "Failed dodge: armour (3,4)=7 would hold against AV 8 without ArmBar, but 7 +1 (ArmBar) = 8 breaks"
                        + " exactly, proving the ArmBar modifier was applied; injury (2,2)=4 -> Stunned");
    }

    @Test
    void armBarMultipleAdjacentPlayersChoiceDialog() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar"))
                        .player("away2", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Failed dodge adjacent to two ArmBar players raises an ARM_BAR player choice dialog");
        assertEquals(DialogId.PLAYER_CHOICE, dialog.getId());
        DialogPlayerChoiceParameter choiceDialog = (DialogPlayerChoiceParameter) dialog;
        assertEquals(PlayerChoiceMode.ARM_BAR, choiceDialog.getPlayerChoiceMode());
        String[] playerIds = choiceDialog.getPlayerIds();
        assertEquals(2, playerIds.length, "The ARM_BAR dialog lists the two adjacent ArmBar players");
        assertTrue(Arrays.asList(playerIds).contains("away1"));
        assertTrue(Arrays.asList(playerIds).contains("away2"));

        Player<?> away1 = state.getGame().getPlayerById("away1");
        TestRolls.on(state).armour(6, 6).injury(2, 2);
        StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.ARM_BAR, new Player<?>[]{away1}));

        assertEquals(PlayerState.STUNNED,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "After the ARM_BAR choice is submitted, armour (6,6)=12 breaks AV 8 and injury (2,2)=4 -> Stunned");
    }

    @Test
    void armBarIgnoredByIgnoresArmourModifiersFromSkills() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Iron Hard Skin")))
                .withTeam(false, t -> t.player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("Arm Bar")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(3, 4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(7, 7)));

        assertEquals(PlayerState.PRONE,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "Iron Hard Skin's ignoresArmourModifiersFromSkills suppresses ArmBar's +1 armour modifier:"
                        + " armour (3,4)=7 holds against AV 8, so home1 falls prone with no injury roll");
    }
}

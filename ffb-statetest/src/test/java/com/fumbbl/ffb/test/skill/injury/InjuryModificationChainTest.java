package com.fumbbl.ffb.test.skill.injury;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogInjuryModificationChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InjuryModificationOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandInjuryModificationChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InjuryModificationChainTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void crushingBlowBreaksArmourWhenAccepted() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Crushing Blow"), defender -> {
		}, 9);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		IStep step = blockBothDown(state);

		assertEquals(StepId.HANDLE_DROP_PLAYER_CONTEXT, step.getId());
		DialogSkillUseParameter dialog = skillUseDialog(state);
		assertEquals("home1", dialog.getPlayerId());
		assertEquals("Crushing Blow", dialog.getSkill().getName());

		StepEngine.respond(state, useSkill(state, "home1", "Crushing Blow", true));

		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
		assertTrue(skillUsed(state, "home1", "Crushing Blow"));
	}

	@Test
	public void declinedCrushingBlowKeepsArmourIntact() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Crushing Blow"), defender -> {
		}, 9);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		StepEngine.respond(state, useSkill(state, "home1", "Crushing Blow", false));

		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
		assertFalse(skillUsed(state, "home1", "Crushing Blow"));
		assertNull(state.getGame().getDialogParameter());
	}

	@Test
	public void crushingBlowThenDwarfenGritReRollKeepsModifier() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Crushing Blow"),
			defender -> defender.skill("Dwarfen Grit"), 9);
		// root armour 8 holds; CB child: 9 breaks -> injury roll; DG child re-roll 4+3+1 = 8 holds
		TestRolls.on(state).armor(4, 4).injury(2, 2).armor(4, 3);
		blockBothDown(state);

		StepEngine.respond(state, useSkill(state, "home1", "Crushing Blow", true));

		DialogSkillUseParameter dialog = skillUseDialog(state);
		assertEquals("away1", dialog.getPlayerId());
		assertEquals("Dwarfen Grit", dialog.getSkill().getName());

		StepEngine.respond(state, useSkill(state, "away1", "Dwarfen Grit", true));

		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
		assertTrue(skillUsed(state, "away1", "Dwarfen Grit"));
		assertTrue(skillUsed(state, "home1", "Crushing Blow"));
	}

	@Test
	public void dwarfenGritReRollBreakingAgainIsKept() {
		GameState state = setUpBothDown(attacker -> {
		}, defender -> defender.skill("Dwarfen Grit"), 8);
		// root armour 8 breaks -> injury 4; DG re-roll 5 holds
		TestRolls.on(state).armor(4, 4).injury(2, 2).armor(3, 2);
		blockBothDown(state);

		assertEquals("Dwarfen Grit", skillUseDialog(state).getSkill().getName());
		StepEngine.respond(state, useSkill(state, "away1", "Dwarfen Grit", true));

		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
	}

	@Test
	public void krumpAndSmashReRollBreakingArmourDoesNotOfferDwarfenGrit() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Krump and Smash"),
			defender -> defender.skill("Dwarfen Grit"), 9);
		// root 4 holds; K&S re-roll 10 breaks -> injury 4
		TestRolls.on(state).armor(2, 2).armor(5, 5).injury(2, 2);
		blockBothDown(state);

		assertEquals("Krump and Smash", skillUseDialog(state).getSkill().getName());
		StepEngine.respond(state, useSkill(state, "home1", "Krump and Smash", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
		assertFalse(skillUsed(state, "away1", "Dwarfen Grit"));
	}

	@Test
	public void declineIsNotAskedAgainForTheSameNode() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Crushing Blow").skill("Krump and Smash"),
			defender -> {
			}, 9);
		TestRolls.on(state).armor(4, 4).armor(1, 1).injury(2, 2);
		blockBothDown(state);

		DialogInjuryModificationChoiceParameter dialog =
			(DialogInjuryModificationChoiceParameter) state.getGame().getDialogParameter();
		assertNotNull(dialog);
		assertEquals(2, dialog.getOptions().size());

		StepEngine.respond(state, new ClientCommandInjuryModificationChoice(Collections.emptyList()));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
		assertFalse(skillUsed(state, "home1", "Crushing Blow"));
		assertFalse(skillUsed(state, "home1", "Krump and Smash"));
	}

	@Test
	public void selectionDialogAcceptsOfferedOptionOnly() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Crushing Blow").skill("Krump and Smash"),
			defender -> {
			}, 9);
		TestRolls.on(state).armor(4, 4).armor(1, 1).injury(2, 2);
		blockBothDown(state);

		Skill dwarfenGrit = skill(state, "Dwarfen Grit");
		StepEngine.respond(state, new ClientCommandInjuryModificationChoice(Collections.singletonList(dwarfenGrit)));
		assertNotNull(state.getGame().getDialogParameter(), "Unknown option must be rejected");

		DialogInjuryModificationChoiceParameter dialog =
			(DialogInjuryModificationChoiceParameter) state.getGame().getDialogParameter();
		InjuryModificationOption crushingBlow = dialog.getOptions().stream()
			.filter(option -> option.getSkills().size() == 1 && option.getSkills().get(0).getName().equals("Crushing Blow"))
			.findFirst().orElseThrow(IllegalStateException::new);
		StepEngine.respond(state, new ClientCommandInjuryModificationChoice(crushingBlow.getSkills()));

		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
		assertTrue(skillUsed(state, "home1", "Crushing Blow"));
		assertFalse(skillUsed(state, "home1", "Krump and Smash"));
	}

	@Test
	public void savageMaulingReRollsInjuryOnce() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Savage Mauling"), defender -> {
		}, 8);
		// armour 8 breaks, injury 4 stunned, re-roll 8 KO
		TestRolls.on(state).armor(4, 4).injury(2, 2).injury(4, 4);
		blockBothDown(state);

		assertEquals("Savage Mauling", skillUseDialog(state).getSkill().getName());
		StepEngine.respond(state, useSkill(state, "home1", "Savage Mauling", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.KNOCKED_OUT, playerState(state, "away1").getBase());
	}

	@Test
	public void noModificationSkillsResolveWithoutDialog() {
		GameState state = setUpBothDown(attacker -> {
		}, defender -> {
		}, 8);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
	}

	@Test
	public void mightyBlowIsAppliedAutomaticallyWhenNotOptional() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalMightyBlowBreaksArmourWhenAccepted() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		DialogSkillUseParameter dialog = skillUseDialog(state);
		assertEquals("home1", dialog.getPlayerId());
		assertEquals("Mighty Blow", dialog.getSkill().getName());

		StepEngine.respond(state, useSkill(state, "home1", "Mighty Blow", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
	}

	@Test
	public void declinedOptionalMightyBlowKeepsArmourIntact() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		StepEngine.respond(state, useSkill(state, "home1", "Mighty Blow", false));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalMightyBlowIsOfferedOnInjuryWhenArmourBrokeUnmodified() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		// armour 10 breaks, injury 7 stunned, +1 knocks out
		TestRolls.on(state).armor(5, 5).injury(4, 3);
		blockBothDown(state);

		assertEquals("Mighty Blow", skillUseDialog(state).getSkill().getName());
		StepEngine.respond(state, useSkill(state, "home1", "Mighty Blow", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.KNOCKED_OUT, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalMightyBlowIsNotOfferedWhenItChangesNothing() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		// armour 4 holds even with +1
		TestRolls.on(state).armor(2, 2);
		blockBothDown(state);

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalClawsBreaksArmourWhenAccepted() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Claws"), defender -> {
		}, 10);
		enable(state, GameOptionId.CLAWS_OPTIONAL);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		assertEquals("Claws", skillUseDialog(state).getSkill().getName());
		StepEngine.respond(state, useSkill(state, "home1", "Claws", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
	}

	@Test
	public void declinedOptionalClawsKeepsArmourIntact() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Claws"), defender -> {
		}, 10);
		enable(state, GameOptionId.CLAWS_OPTIONAL);
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		StepEngine.respond(state, useSkill(state, "home1", "Claws", false));

		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalMightyBlowAcceptedOnArmourIsNotOfferedOnInjury() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow"), defender -> {
		}, 9);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		// armour 8 + 1 breaks, injury 7 stunned would become KO with another +1
		TestRolls.on(state).armor(4, 4).injury(4, 3);
		blockBothDown(state);

		StepEngine.respond(state, useSkill(state, "home1", "Mighty Blow", true));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
	}

	@Test
	public void optionalMightyBlowAndCrushingBlowAreOfferedOnlyCombinedWhenBothAreNeeded() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Mighty Blow").skill("Crushing Blow"), defender -> {
		}, 10);
		enable(state, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		// armour 8 needs +2
		TestRolls.on(state).armor(4, 4).injury(2, 2);
		blockBothDown(state);

		assertTrue(state.getGame().getDialogParameter() instanceof DialogInjuryModificationChoiceParameter,
			"Expected selection dialog, was " + state.getGame().getDialogParameter());
		DialogInjuryModificationChoiceParameter dialog =
			(DialogInjuryModificationChoiceParameter) state.getGame().getDialogParameter();
		assertEquals(1, dialog.getOptions().size());
		assertEquals(2, dialog.getOptions().get(0).getSkills().size());

		StepEngine.respond(state, new ClientCommandInjuryModificationChoice(dialog.getOptions().get(0).getSkills()));

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.STUNNED, playerState(state, "away1").getBase());
		assertTrue(skillUsed(state, "home1", "Crushing Blow"));
	}

	@Test
	public void optionalClawsIsNotOfferedAgainstIronHardSkin() {
		GameState state = setUpBothDown(attacker -> attacker.skill("Claws"), defender -> defender.skill("Iron Hard Skin"),
			10);
		enable(state, GameOptionId.CLAWS_OPTIONAL);
		TestRolls.on(state).armor(4, 4);
		blockBothDown(state);

		assertNull(state.getGame().getDialogParameter());
		assertEquals(PlayerState.PRONE, playerState(state, "away1").getBase());
	}

	private void enable(GameState state, GameOptionId optionId) {
		state.getGame().getOptions().addOption(new GameOptionBoolean(optionId).setValue(true));
	}

	private GameState setUpBothDown(Consumer<GameStateBuilder.PlayerDef> attacker,
		Consumer<GameStateBuilder.PlayerDef> defender, int defenderArmour) {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, team -> team
				.player("home1", player -> {
					player.at(7, 7).stats(6, 3, 3, 5, 8).skill("Block");
					attacker.accept(player);
				}))
			.withTeam(false, team -> team
				.player("away1", player -> {
					player.at(8, 7).stats(6, 3, 3, 5, defenderArmour);
					defender.accept(player);
				}))
			.build();
		TestRolls.on(state).block("bothdown");
		return state;
	}

	private IStep blockBothDown(GameState state) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		StepEngine.respond(state, Commands.block("home1", "away1"));
		return StepEngine.respond(state, Commands.blockChoice(0));
	}

	private DialogSkillUseParameter skillUseDialog(GameState state) {
		assertTrue(state.getGame().getDialogParameter() instanceof DialogSkillUseParameter,
			"Expected skill use dialog, was " + state.getGame().getDialogParameter());
		return (DialogSkillUseParameter) state.getGame().getDialogParameter();
	}

	private ClientCommandUseSkill useSkill(GameState state, String playerId, String skillName, boolean use) {
		return new ClientCommandUseSkill(skill(state, skillName), use, playerId, null, false);
	}

	private Skill skill(GameState state, String skillName) {
		return state.getGame().getRules().getSkillFactory().forName(skillName);
	}

	private boolean skillUsed(GameState state, String playerId, String skillName) {
		Player<?> player = state.getGame().getPlayerById(playerId);
		return player.isUsed(player.getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> skill.getName().equals(skillName)).findFirst().orElseThrow(IllegalStateException::new));
	}

	private PlayerState playerState(GameState state, String playerId) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(playerId));
	}
}

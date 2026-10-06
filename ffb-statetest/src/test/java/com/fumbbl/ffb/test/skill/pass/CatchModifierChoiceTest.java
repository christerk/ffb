package com.fumbbl.ffb.test.skill.pass;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.bb2025.shared.StepCatchScatterThrowIn;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatchModifierChoiceTest {
	private static final FieldCoordinate BOUNCE_TARGET = new FieldCoordinate(11, 6);

	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	/**
	 * The hand-off target fails its catch, the ball bounces north onto the receiver who catches it loose.
	 */
	private GameState looseBall(String... receiverSkills) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(10, 7)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", player -> player.at(11, 7).stats(6, 3, 3, 5, 8))
				.player("receiver", player -> {
					player.at(11, 6).stats(6, 3, 3, 5, 8);
					for (String skill : receiverSkills) {
						player.skill(skill);
					}
				}))
			.withTeam(false, team -> team.player("opponent", player -> player.at(20, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private GameState handOff(String... catcherSkills) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(10, 7)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", player -> {
					player.at(11, 7).stats(6, 3, 3, 5, 8);
					for (String skill : catcherSkills) {
						player.skill(skill);
					}
				}))
			.withTeam(false, team -> team.player("opponent", player -> player.at(20, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private void throwHandOff(GameState state, int... rolls) {
		TestRolls.on(state).general(rolls);
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.HAND_OVER_MOVE));
		StepEngine.respond(state, Commands.handOver("thrower", "catcher"));
	}

	private Skill skill(GameState state, String name) {
		return state.getGame().getRules().getSkillFactory().forName(name);
	}

	private Skill professional(GameState state) {
		return skill(state, "Consummate Professional");
	}

	private DialogReRollModifierChoiceParameter dialog(GameState state) {
		return (DialogReRollModifierChoiceParameter) state.getGame().getDialogParameter();
	}

	private void chooseModifier(GameState state, String playerId) {
		StepEngine.respond(state, Commands.reRollModifierChoice(playerId, ReRolledActions.CATCH, professional(state)));
	}

	private boolean hasBall(GameState state, String playerId) {
		return UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById(playerId));
	}

	@Test
	void successfulCatchDoesNotSpendOrOfferModifier() {
		GameState state = looseBall("Consummate Professional");
		throwHandOff(state, 1, 1, 6);
		assertNull(state.getGame().getDialogParameter());
		assertTrue(hasBall(state, "receiver"));
		assertFalse(state.getGame().getPlayerById("receiver").isUsed(professional(state)));
	}

	@Test
	void failedLooseBallCatchOffersModifierAndChoosingItRescuesOriginalDie() {
		GameState state = looseBall("Consummate Professional");
		throwHandOff(state, 1, 1, 3);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertNotNull(dialog);
		assertEquals(ReRolledActions.CATCH, dialog.getReRolledAction());
		assertEquals("receiver", dialog.getPlayerId());
		assertEquals(4, dialog.getMinimumRoll());
		assertEquals(3, dialog.getRoll());
		assertEquals("Consummate Professional", dialog.getModifierOptions().get(0).getLabel());
		assertEquals(3, dialog.getModifierOptions().get(0).getMinimumRoll());
		assertNull(dialog.getReRollSkill());

		chooseModifier(state, "receiver");

		assertTrue(hasBall(state, "receiver"));
		assertTrue(state.getGame().getPlayerById("receiver").isUsed(professional(state)));
	}

	@Test
	void decliningModifierLetsTheBallBounceOnWithoutSpendingIt() {
		GameState state = looseBall("Consummate Professional");
		// the ball bounces on to the north again
		throwHandOff(state, 1, 1, 3, 1);
		assertNotNull(dialog(state));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.CATCH, null));
		assertFalse(hasBall(state, "receiver"));
		assertFalse(state.getGame().getPlayerById("receiver").isUsed(professional(state)));
	}

	@Test
	void naturalOneOnLooseBallCannotBeRescued() {
		GameState state = looseBall("Consummate Professional");
		throwHandOff(state, 1, 1, 1, 1);
		assertNull(state.getGame().getDialogParameter());
		assertFalse(state.getGame().getPlayerById("receiver").isUsed(professional(state)));
	}

	@Test
	void catchReRollIsOfferedAsChoiceOnLooseBall() {
		GameState state = looseBall("Catch");
		throwHandOff(state, 1, 1, 3);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertNotNull(dialog);
		assertEquals("Catch", dialog.getReRollSkill().getName());
		assertTrue(dialog.getModifierOptions().isEmpty());
		assertFalse(dialog.hasProperty(ReRollProperty.TRR));

		TestRolls.on(state).general(5);
		StepEngine.respond(state,
			new ClientCommandUseSkill(skill(state, "Catch"), true, "receiver", ReRolledActions.CATCH, false));

		assertTrue(hasBall(state, "receiver"));
	}

	@Test
	void decliningCatchReRollOnLooseBallDoesNotUseIt() {
		GameState state = looseBall("Catch");
		throwHandOff(state, 1, 1, 3, 1);
		assertNotNull(dialog(state));
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.CATCH, null));
		assertFalse(hasBall(state, "receiver"));
		assertFalse(state.getGame().getPlayerById("receiver").isUsed(skill(state, "Catch")));
	}

	@Test
	void catchReRollStaysAutomaticForHandOff() {
		GameState state = handOff("Catch");
		throwHandOff(state, 1, 5);
		assertNull(state.getGame().getDialogParameter());
		assertTrue(hasBall(state, "catcher"));
		assertEquals(new FieldCoordinate(11, 7), state.getGame().getFieldModel().getBallCoordinate());
	}

	@Test
	void catchAndModifierAreOfferedTogetherOnLooseBall() {
		GameState state = looseBall("Catch", "Consummate Professional");
		throwHandOff(state, 1, 1, 3);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertEquals("Catch", dialog.getReRollSkill().getName());
		assertEquals(1, dialog.getModifierOptions().size());

		chooseModifier(state, "receiver");

		assertTrue(hasBall(state, "receiver"));
		assertTrue(state.getGame().getPlayerById("receiver").isUsed(professional(state)));
		assertFalse(state.getGame().getPlayerById("receiver").isUsed(skill(state, "Catch")));
	}

	@Test
	void failedCatchReRollCanStillBeRescuedByModifier() {
		GameState state = handOff("Catch", "Consummate Professional");
		// hand off catch needs 3+, the automatic catch re-roll produces a 2
		throwHandOff(state, 1, 2);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertNotNull(dialog);
		assertEquals(2, dialog.getRoll());
		assertNull(dialog.getReRollSkill());
		assertEquals(1, dialog.getModifierOptions().size());

		chooseModifier(state, "catcher");

		assertTrue(hasBall(state, "catcher"));
		assertTrue(state.getGame().getPlayerById("catcher").isUsed(professional(state)));
	}

	@Test
	void invalidModifierChoiceDoesNotSpendSkillOrChangeRoll() {
		GameState state = looseBall("Catch", "Consummate Professional");
		throwHandOff(state, 1, 1, 3);
		Game game = state.getGame();
		StepEngine.respond(state,
			Commands.reRollModifierChoice("receiver", ReRolledActions.CATCH, skill(state, "Catch")));
		assertEquals(3, dialog(state).getRoll());
		assertFalse(game.getPlayerById("receiver").isUsed(professional(state)));
		chooseModifier(state, "receiver");
		assertTrue(hasBall(state, "receiver"));
	}

	@Test
	void savedStepAndDialogResumeModifierSelectionWithoutAnotherDie() {
		GameState state = looseBall("Consummate Professional");
		throwHandOff(state, 1, 1, 3);
		StepCatchScatterThrowIn step = (StepCatchScatterThrowIn) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		StepCatchScatterThrowIn restored =
			new StepCatchScatterThrowIn(state).initFrom(state.getGame().getRules(), saved);
		assertEquals(saved, restored.toJsonValue());
		step.initFrom(state.getGame().getRules(), restored.toJsonValue());
		DialogReRollModifierChoiceParameter restoredDialog = new DialogReRollModifierChoiceParameter();
		restoredDialog.initFrom(state.getGame().getRules(), dialog(state).toJsonValue());
		state.getGame().setDialogParameter(restoredDialog);

		chooseModifier(state, "receiver");

		assertTrue(hasBall(state, "receiver"));
		JsonObject committed = step.toJsonValue();
		assertEquals(1,
			IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(state.getGame().getRules(), committed).size());
	}
}

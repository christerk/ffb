package com.fumbbl.ffb.test.pass;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.bb2025.pass.StepIntercept;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterceptionModifierChoiceTest {
	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	private GameState buildState(String... interceptorSkills) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(10, 7)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(10, 7).stats(6, 3, 3, 5, 8))
				.player("catcher", player -> player.at(16, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, team -> team.player("interceptor", player -> {
				player.at(13, 7).stats(6, 3, 3, 5, 8);
				for (String skill : interceptorSkills) {
					player.skill(skill);
				}
			}))
			.build();
	}

	private Skill professional(GameState state) {
		return state.getGame().getRules().getSkillFactory().forName("Consummate Professional");
	}

	private DialogReRollModifierChoiceParameter dialog(GameState state) {
		return (DialogReRollModifierChoiceParameter) state.getGame().getDialogParameter();
	}

	/**
	 * Throws an accurate pass the opponent may intercept and declares the interception, so that the given rolls are
	 * consumed as interception rolls.
	 */
	private void passAndIntercept(GameState state, int... interceptionRolls) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.PASS));
		// accurate pass
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.pass("thrower", new FieldCoordinate(16, 7)));
		TestRolls.on(state).general(interceptionRolls);
		StepEngine.respond(state, Commands.interceptorChoice("interceptor"));
	}

	private void chooseModifier(GameState state) {
		StepEngine.respond(state,
			Commands.reRollModifierChoice("interceptor", ReRolledActions.INTERCEPTION, professional(state)));
	}

	@Test
	void successfulInterceptionDoesNotSpendOrOfferModifier() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 6);
		assertFalse(state.getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
		assertFalse(state.getGame().getPlayerById("interceptor").isUsed(professional(state)));
	}

	@Test
	void failedInterceptionOffersModifierToInterceptorAndRescuesOriginalDie() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 5);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertEquals(ReRolledActions.INTERCEPTION, dialog.getReRolledAction());
		assertEquals("interceptor", dialog.getPlayerId());
		assertEquals(6, dialog.getMinimumRoll());
		assertEquals(5, dialog.getRoll());
		assertEquals(1, dialog.getModifierOptions().size());
		assertEquals("Consummate Professional", dialog.getModifierOptions().get(0).getLabel());
		assertEquals(5, dialog.getModifierOptions().get(0).getMinimumRoll());
		assertNull(dialog.getReRollSkill());

		chooseModifier(state);

		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
		assertTrue(state.getGame().getPlayerById("interceptor").isUsed(professional(state)));
	}

	@Test
	void decliningTheModifierFailsTheInterceptionWithoutSpendingIt() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.INTERCEPTION, null));
		assertFalse(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
		assertFalse(state.getGame().getPlayerById("interceptor").isUsed(professional(state)));
	}

	@Test
	void interceptorWithoutModifierIsNotAskedAtAll() {
		GameState state = buildState();
		passAndIntercept(state, 5);
		assertFalse(state.getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter);
		assertFalse(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
	}

	@Test
	void usedProfessionalCannotRescueAnotherInterception() {
		GameState state = buildState("Consummate Professional");
		StepEngine.start(state);
		state.getGame().getPlayerById("interceptor").markUsed(professional(state), state.getGame());
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.PASS));
		TestRolls.on(state).general(6);
		StepEngine.respond(state, Commands.pass("thrower", new FieldCoordinate(16, 7)));
		TestRolls.on(state).general(5);
		StepEngine.respond(state, Commands.interceptorChoice("interceptor"));
		assertFalse(state.getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter);
		assertFalse(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
	}

	@Test
	void naturalOneCannotBeRescued() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 1);
		assertFalse(state.getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter);
		assertFalse(state.getGame().getPlayerById("interceptor").isUsed(professional(state)));
	}

	@Test
	void mismatchedModifierSelectionsAreRejected() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 5);
		Skill skill = professional(state);
		StepEngine.respond(state, Commands.reRollModifierChoice("thrower", ReRolledActions.INTERCEPTION, skill));
		StepEngine.respond(state, Commands.reRollModifierChoice("interceptor", ReRolledActions.PICK_UP, skill));
		StepEngine.respond(state, Commands.reRollModifierChoice("interceptor", ReRolledActions.INTERCEPTION));
		assertEquals(5, dialog(state).getRoll());
		assertFalse(state.getGame().getPlayerById("interceptor").isUsed(skill));
		chooseModifier(state);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("interceptor")));
	}

	@Test
	void savedStepAndDialogResumeModifierSelectionWithoutAnotherDie() {
		GameState state = buildState("Consummate Professional");
		passAndIntercept(state, 5);
		Game game = state.getGame();
		StepIntercept step = (StepIntercept) state.getCurrentStep();
		JsonObject saved = step.toJsonValue();
		StepIntercept restored = new StepIntercept(state).initFrom(game.getRules(), saved);
		assertEquals(saved, restored.toJsonValue());
		step.initFrom(game.getRules(), restored.toJsonValue());
		DialogReRollModifierChoiceParameter restoredDialog = new DialogReRollModifierChoiceParameter();
		restoredDialog.initFrom(game.getRules(), dialog(state).toJsonValue());
		game.setDialogParameter(restoredDialog);

		chooseModifier(state);

		assertTrue(UtilPlayer.hasBall(game, game.getPlayerById("interceptor")));
	}
}

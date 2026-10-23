package com.fumbbl.ffb.test.skill.ttm;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.ttm.StepRightStuff;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RightStuffModifierChoiceTest {

	private static final FieldCoordinate TARGET = new FieldCoordinate(15, 7);

	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	private GameState buildState(String... thrownPlayerSkills) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(3, 3)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(12, 7).stats(6, 5, 3, 4, 9).skill("Throw Team-Mate"))
				.player("stunty", player -> {
					player.at(12, 8).stats(6, 2, 3, 0, 7).skill("Right Stuff");
					for (String skill : thrownPlayerSkills) {
						player.skill(skill);
					}
				}))
			.withTeam(false, team -> team.player("opponent", player -> player.at(3, 3).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private void throwTeamMate(GameState state, int landingRoll, int... followUpRolls) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE));
		StepEngine.respond(state, Commands.pickUpTeamMate("thrower", "stunty"));
		TestRolls.on(state).general(6, landingRoll).general(followUpRolls);
		TestRolls.on(state).direction("east", "east", "east");
		StepEngine.respond(state, Commands.throwTeamMate("thrower", TARGET));
	}

	private Skill professional(GameState state) {
		return state.getGame().getRules().getSkillFactory().forName("Consummate Professional");
	}

	private DialogReRollModifierChoiceParameter dialog(GameState state) {
		return (DialogReRollModifierChoiceParameter) state.getGame().getDialogParameter();
	}

	private void chooseModifier(GameState state) {
		StepEngine.respond(state,
			Commands.reRollModifierChoice("stunty", ReRolledActions.RIGHT_STUFF, professional(state)));
	}

	private boolean landedStanding(GameState state) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById("stunty")).getBase() == PlayerState.STANDING;
	}

	@Test
	void successfulLandingDoesNotOfferOrSpendModifier() {
		GameState state = buildState("Consummate Professional");
		throwTeamMate(state, 3);
		assertNull(state.getGame().getDialogParameter());
		assertTrue(landedStanding(state));
		assertFalse(state.getGame().getPlayerById("stunty").isUsed(professional(state)));
	}

	@Test
	void failedLandingOffersModifierAndChoosingItRescuesOriginalDie() {
		GameState state = buildState("Consummate Professional");
		throwTeamMate(state, 2);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertEquals(ReRolledActions.RIGHT_STUFF, dialog.getReRolledAction());
		assertEquals("stunty", dialog.getPlayerId());
		assertEquals(2, dialog.getRoll());
		assertEquals(3, dialog.getMinimumRoll());
		assertEquals("Consummate Professional", dialog.getModifierOptions().get(0).getLabel());
		assertEquals(2, dialog.getModifierOptions().get(0).getMinimumRoll());
		assertNull(dialog.getReRollSkill());

		chooseModifier(state);

		assertTrue(landedStanding(state));
		assertTrue(state.getGame().getPlayerById("stunty").isUsed(professional(state)));
	}

	@Test
	void decliningTheModifierDropsThePlayerWithoutSpendingIt() {
		GameState state = buildState("Consummate Professional");
		throwTeamMate(state, 2);
		TestRolls.on(state).general(1, 1);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.RIGHT_STUFF, null));
		assertFalse(landedStanding(state));
		assertFalse(state.getGame().getPlayerById("stunty").isUsed(professional(state)));
	}

	@Test
	void usedProfessionalIsNotOfferedOnLanding() {
		GameState state = buildState("Consummate Professional");
		state.getGame().getPlayerById("stunty").markUsed(professional(state), state.getGame());
		throwTeamMate(state, 2, 1, 1);
		assertNull(state.getGame().getDialogParameter());
		assertFalse(landedStanding(state));
	}

	private StepRightStuff swoopLanding(GameState state, int landingRoll) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE));
		StepRightStuff step = new StepRightStuff(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.GOTO_LABEL_ON_SUCCESS, "success"));
		step.init(parameters);
		step.setParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_ID, "stunty"));
		step.setParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_HAS_BALL, false));
		step.setParameter(StepParameter.from(StepParameterKey.PASS_RESULT, PassResult.ACCURATE));
		step.setParameter(new StepParameter(StepParameterKey.USING_SWOOP, true));
		step.setParameter(
			new StepParameter(StepParameterKey.OLD_DEFENDER_STATE, new PlayerState(PlayerState.STANDING)));
		TestRolls.on(state).general(landingRoll);
		step.start();
		return step;
	}

	@Test
	void swoopReRollIsOfferedAlongsideTheModifier() {
		GameState state = buildState("Swoop", "Consummate Professional");
		swoopLanding(state, 2);
		DialogReRollModifierChoiceParameter dialog = dialog(state);
		assertEquals("Swoop", dialog.getReRollSkill().getName());
		assertEquals(1, dialog.getModifierOptions().size());
		assertEquals("Consummate Professional", dialog.getModifierOptions().get(0).getLabel());
	}

	@Test
	void loneSwoopReRollIsUsedWithoutAskingTheCoach() {
		GameState state = buildState("Swoop");
		StepRightStuff step = swoopLanding(state, 2);
		assertNull(state.getGame().getDialogParameter());
		assertEquals(StepAction.REPEAT, step.getResult().getNextAction());
		assertEquals(ReRollSources.SWOOP, step.getReRollSource());
	}

	@Test
	void withoutProfessionalNoDialogIsShown() {
		GameState state = buildState();
		throwTeamMate(state, 2, 1, 1);
		assertNull(state.getGame().getDialogParameter());
		assertFalse(landedStanding(state));
	}
}

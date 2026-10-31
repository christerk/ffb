package com.fumbbl.ffb.test.reroll;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the team re-roll sources sent by the re-roll dialogs, using a failed Jump Up roll (4+ needed).
 */
class TeamReRollSourceTest {
	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	private GameState buildState(String... skills) {
		return new GameStateBuilder(server.getGameState()).withRule("BB2025")
			.withTeam(true, team -> team.player("jumper", player -> {
				player.at(7, 7).stats(6, 3, 4, 5, 8).skill("Jump Up")
					.state(new PlayerState(PlayerState.PRONE).changeActive(true));
				for (String skill : skills) {
					player.skill(skill);
				}
			}).player("teammate", player -> player.at(10, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, team -> team.player("opponent", player -> player.at(8, 7).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private void failJumpUp(GameState state, int... rolls) {
		StepEngine.start(state);
		TestRolls.on(state).general(rolls).block("pushback");
		StepEngine.respond(state, Commands.selectPlayer("jumper", PlayerAction.BLOCK));
		StepEngine.respond(state, Commands.block("jumper", "opponent"));
	}

	private void reRoll(GameState state, ReRollSource reRollSource) {
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, reRollSource));
	}

	private HasReRollProperties dialog(GameState state) {
		return (HasReRollProperties) state.getGame().getDialogParameter();
	}

	private TurnData turnData(GameState state) {
		return state.getGame().getTurnData();
	}

	private void addMascot(GameState state) {
		InducementType mascot = state.getGame().<InducementTypeFactory>getFactory(Factory.INDUCEMENT_TYPE)
			.forName("teamMascot");
		turnData(state).getInducementSet().addInducement(new Inducement(mascot, 1));
	}

	private boolean stoodUp(GameState state) {
		return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("jumper")).getBase()
			== PlayerState.MOVING;
	}

	@Test
	void singleUseReRollIsOfferedAndConsumedExplicitly() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setReRollsBrilliantCoachingOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertTrue(dialog(state).hasProperty(ReRollProperty.BRILLIANT_COACHING));
		reRoll(state, ReRollSources.BRILLIANT_COACHING);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsBrilliantCoachingOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void teamReRollDoesNotConsumeSingleUseReRoll() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(1, turnData(state).getReRollShowStarOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void teamReRollFailsIfOnlySingleUseReRollIsLeft() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		turnData(state).setReRollsPumpUpTheCrowdOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertFalse(stoodUp(state));
		assertEquals(1, turnData(state).getReRollsPumpUpTheCrowdOneDrive());
		assertEquals(1, turnData(state).getReRolls());
		assertEquals(4, state.getDiceRoller().rollSkill());
	}

	@Test
	void unavailableSingleUseReRollFails() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.SHOW_STAR);
		assertFalse(stoodUp(state));
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void teamReRollUsesLeaderFirst() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setLeaderState(LeaderState.AVAILABLE);
		failJumpUp(state, 1, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.LEADER));
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(LeaderState.USED, turnData(state).getLeaderState());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void failedMascotFallsBackToSelectedSingleUseReRoll() {
		GameState state = buildState();
		addMascot(state);
		turnData(state).setReRolls(2);
		turnData(state).setReRollsBrilliantCoachingOneDrive(1);
		failJumpUp(state, 1, 3, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.MASCOT));
		reRoll(state, ReRollSources.MASCOT_BRILLIANT_COACHING);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsBrilliantCoachingOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void successfulMascotKeepsFallbackReRoll() {
		GameState state = buildState();
		addMascot(state);
		turnData(state).setReRolls(1);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 4, 4, 5);
		reRoll(state, ReRollSources.MASCOT_SHOW_STAR);
		assertTrue(stoodUp(state));
		assertEquals(1, turnData(state).getReRollShowStarOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void failedProFallsBackToSelectedSingleUseReRoll() {
		GameState state = buildState("Pro");
		turnData(state).setReRolls(1);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 1, 3, 4, 5);
		reRoll(state, ReRollSources.PRO_SHOW_STAR);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollShowStarOneDrive());
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void failedProAndMascotFallBackToSelectedSingleUseReRoll() {
		GameState state = buildState("Pro");
		addMascot(state);
		turnData(state).setReRolls(1);
		turnData(state).setReRollsPumpUpTheCrowdOneDrive(1);
		failJumpUp(state, 1, 1, 3, 3, 4, 5);
		reRoll(state, ReRollSources.PRO_MASCOT_PUMP_UP_THE_CROWD);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsPumpUpTheCrowdOneDrive());
		assertEquals(0, turnData(state).getReRolls());
	}
}

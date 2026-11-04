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

	private Inducement addMascot(GameState state) {
		InducementType mascotType = state.getGame().<InducementTypeFactory>getFactory(Factory.INDUCEMENT_TYPE)
			.forName("teamMascot");
		Inducement mascot = new Inducement(mascotType, 1);
		turnData(state).getInducementSet().addInducement(mascot);
		return mascot;
	}

	/**
	 * Makes the mascot unavailable after the dialog offered it, so only the use time check can catch it.
	 */
	private void loseMascot(GameState state, Inducement mascot) {
		assertTrue(dialog(state).hasProperty(ReRollProperty.MASCOT));
		turnData(state).getInducementSet().removeInducement(mascot);
	}

	private boolean stoodUp(GameState state) {
		return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("jumper")).getBase()
			== PlayerState.MOVING;
	}

	@Test
	void driveBonusReRollIsOfferedAndConsumedExplicitly() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setReRollsBrilliantCoachingOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertTrue(dialog(state).hasProperty(ReRollProperty.DRIVE_BONUS));
		reRoll(state, ReRollSources.DRIVE_BONUS_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsBrilliantCoachingOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void teamReRollDoesNotConsumeDriveBonusReRoll() {
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
	void teamReRollFallsBackToDriveBonusReRollIfNoOtherIsLeft() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		turnData(state).setReRollsPumpUpTheCrowdOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsPumpUpTheCrowdOneDrive());
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void teamReRollPrefersLeaderOverDriveBonusReRoll() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setLeaderState(LeaderState.AVAILABLE);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(LeaderState.USED, turnData(state).getLeaderState());
		assertEquals(1, turnData(state).getReRollShowStarOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void unavailableMascotFallsBackToTeamReRoll() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		Inducement mascot = addMascot(state);
		failJumpUp(state, 1, 4, 5);
		loseMascot(state, mascot);
		reRoll(state, ReRollSources.MASCOT_TRR);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void unavailableMascotAfterFailedProFallsBackToTeamReRoll() {
		GameState state = buildState("Pro");
		turnData(state).setReRolls(1);
		Inducement mascot = addMascot(state);
		// jump up 1, pro 1, pro 3, jump up 4
		failJumpUp(state, 1, 1, 3, 4, 5);
		loseMascot(state, mascot);
		reRoll(state, ReRollSources.PRO_MASCOT_TRR);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void unavailableMascotWithoutFallbackFails() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		Inducement mascot = addMascot(state);
		failJumpUp(state, 1, 4, 5);
		loseMascot(state, mascot);
		reRoll(state, ReRollSources.MASCOT);
		assertFalse(stoodUp(state));
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void driveBonusReRollUsesAnyAvailableType() {
		GameState state = buildState();
		turnData(state).setReRolls(2);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.DRIVE_BONUS_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollShowStarOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void unavailableDriveBonusReRollFails() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		turnData(state).setReRollsBrilliantCoachingOneDrive(1);
		failJumpUp(state, 1, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.DRIVE_BONUS));
		turnData(state).setReRollsBrilliantCoachingOneDrive(0);
		reRoll(state, ReRollSources.DRIVE_BONUS_RE_ROLL);
		assertFalse(stoodUp(state));
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void driveBonusReRollNotOfferedIsIgnored() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		assertFalse(dialog(state).hasProperty(ReRollProperty.DRIVE_BONUS));
		reRoll(state, ReRollSources.DRIVE_BONUS_RE_ROLL);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertEquals(1, turnData(state).getReRolls());
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void mascotNotOfferedIsIgnored() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		assertFalse(dialog(state).hasProperty(ReRollProperty.MASCOT));
		reRoll(state, ReRollSources.MASCOT_TRR);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void proNotOfferedIsIgnored() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.PRO_TRR);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void reRollForOtherActionIsIgnored() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.DODGE, ReRollSources.TEAM_RE_ROLL));
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertEquals(1, turnData(state).getReRolls());
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void skillReRollNotOfferedIsIgnored() {
		GameState state = buildState();
		turnData(state).setReRolls(1);
		failJumpUp(state, 1, 4, 5);
		reRoll(state, ReRollSources.DODGE);
		assertTrue(dialog(state).hasProperty(ReRollProperty.TRR));
		assertEquals(1, turnData(state).getReRolls());
		reRoll(state, ReRollSources.TEAM_RE_ROLL);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
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
	void failedMascotFallsBackToSelectedDriveBonusReRoll() {
		GameState state = buildState();
		addMascot(state);
		turnData(state).setReRolls(2);
		turnData(state).setReRollsBrilliantCoachingOneDrive(1);
		failJumpUp(state, 1, 3, 4, 5);
		assertTrue(dialog(state).hasProperty(ReRollProperty.MASCOT));
		reRoll(state, ReRollSources.MASCOT_DRIVE_BONUS);
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
		reRoll(state, ReRollSources.MASCOT_DRIVE_BONUS);
		assertTrue(stoodUp(state));
		assertEquals(1, turnData(state).getReRollShowStarOneDrive());
		assertEquals(1, turnData(state).getReRolls());
	}

	@Test
	void failedProFallsBackToSelectedDriveBonusReRoll() {
		GameState state = buildState("Pro");
		turnData(state).setReRolls(1);
		turnData(state).setReRollShowStarOneDrive(1);
		failJumpUp(state, 1, 1, 3, 4, 5);
		reRoll(state, ReRollSources.PRO_DRIVE_BONUS);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollShowStarOneDrive());
		assertEquals(0, turnData(state).getReRolls());
	}

	@Test
	void teamReRollFallbackOnlyReRollsProAfterPassedLoner() {
		GameState state = buildState("Pro", "Loner");
		turnData(state).setReRolls(1);
		// jump up 1, pro 1, loner 4, pro 3, jump up 4
		failJumpUp(state, 1, 1, 4, 3, 4, 5);
		reRoll(state, ReRollSources.PRO_TRR);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void failedLonerKeepsFailedProRoll() {
		GameState state = buildState("Pro", "Loner");
		turnData(state).setReRolls(1);
		// jump up 1, pro 1, loner 1: neither the pro roll nor the jump up roll is re-rolled
		failJumpUp(state, 1, 1, 1, 5);
		reRoll(state, ReRollSources.PRO_TRR);
		assertFalse(stoodUp(state));
		assertEquals(0, turnData(state).getReRolls());
		assertEquals(5, state.getDiceRoller().rollSkill());
	}

	@Test
	void failedProAndMascotFallBackToSelectedDriveBonusReRoll() {
		GameState state = buildState("Pro");
		addMascot(state);
		turnData(state).setReRolls(1);
		turnData(state).setReRollsPumpUpTheCrowdOneDrive(1);
		failJumpUp(state, 1, 1, 3, 3, 4, 5);
		reRoll(state, ReRollSources.PRO_MASCOT_DRIVE_BONUS);
		assertTrue(stoodUp(state));
		assertEquals(0, turnData(state).getReRollsPumpUpTheCrowdOneDrive());
		assertEquals(0, turnData(state).getReRolls());
	}
}

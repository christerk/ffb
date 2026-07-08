package com.fumbbl.ffb.server.mechanic.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.ActiveEffects;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.ServerUtilPlayer;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Empirical checks for the BB2025 "Cheering Fans" additional (offensive) assist.
 * <p>
 * These tests isolate the two mechanisms involved in the reported bug (a defending/kicking
 * team that won Cheering Fans not getting the extra die on its first blitz):
 * <ul>
 *   <li>{@link RollMechanic#getTotalAttackerStrength} - whether the assist is folded into the
 *       attacker strength (and therefore the block-dice count) for a normal blitz.</li>
 *   <li>The removal bookkeeping in {@link ActiveEffects} - whether the winner's assist survives
 *       the opposing (receiving) team's intervening turn.</li>
 * </ul>
 */
@MockitoSettings(strictness = Strictness.LENIENT)
class CheeringFansAdditionalAssistBB2025Test {

	private static final String KICKING_TEAM_ID = "kickingTeam";
	private static final String RECEIVING_TEAM_ID = "receivingTeam";

	private RollMechanic mechanic() {
		return new RollMechanic();
	}

	private Game mockBlitzGame(Team actingTeam, Set<String> multiBlockTargets) {
		Game game = mock(Game.class);
		when(game.getActingTeam()).thenReturn(actingTeam);
		when(game.getMultiBlockTargets()).thenReturn(multiBlockTargets);
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLITZ);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		return game;
	}

	private Player<?> mockPlayer(int strength) {
		Player<?> player = mock(Player.class);
		when(player.getStrengthWithModifiers()).thenReturn(strength);
		when(player.hasSkillProperty(any())).thenReturn(false);
		when(player.hasSkillProperty(NamedProperties.addStrengthOnBlitz)).thenReturn(false);
		return player;
	}

	@Test
	void blitzOnDefenceIncludesAdditionalAssistWhenPresent() {
		Team kickingTeam = mock(Team.class);
		when(kickingTeam.getId()).thenReturn(KICKING_TEAM_ID);

		Game game = mockBlitzGame(kickingTeam, new HashSet<>());
		GameState gameState = mock(GameState.class);
		when(gameState.getGame()).thenReturn(game);
		when(gameState.getAdditionalAssist(KICKING_TEAM_ID)).thenReturn(1);

		Player<?> attacker = mockPlayer(3);
		Player<?> defender = mockPlayer(3);

		try (MockedStatic<ServerUtilPlayer> util = mockStatic(ServerUtilPlayer.class)) {
			// no field assists: block strength stays at the base value it was called with
			util.when(() -> ServerUtilPlayer.findBlockStrength(eq(game), eq(attacker), eq(3), eq(defender), eq(false)))
				.thenReturn(3);

			int total = mechanic().getTotalAttackerStrength(gameState, attacker, defender, false, false, false, 3);

			// base 3 + 1 additional assist -> 4 (> defender 3 => 2 block dice)
			assertEquals(4, total, "A normal blitz for the Cheering-Fans winner must include the additional assist");
		}
	}

	@Test
	void blitzWithoutAssistDoesNotAddStrength() {
		Team kickingTeam = mock(Team.class);
		when(kickingTeam.getId()).thenReturn(KICKING_TEAM_ID);

		Game game = mockBlitzGame(kickingTeam, new HashSet<>());
		GameState gameState = mock(GameState.class);
		when(gameState.getGame()).thenReturn(game);
		when(gameState.getAdditionalAssist(KICKING_TEAM_ID)).thenReturn(0);

		Player<?> attacker = mockPlayer(3);
		Player<?> defender = mockPlayer(3);

		try (MockedStatic<ServerUtilPlayer> util = mockStatic(ServerUtilPlayer.class)) {
			util.when(() -> ServerUtilPlayer.findBlockStrength(eq(game), eq(attacker), eq(3), eq(defender), eq(false)))
				.thenReturn(3);

			int total = mechanic().getTotalAttackerStrength(gameState, attacker, defender, false, false, false, 3);

			assertEquals(3, total, "Without the effect the attacker strength must be unchanged");
		}
	}

	/**
	 * The only branch in {@link RollMechanic#getTotalAttackerStrength} that can *omit* an existing
	 * assist is the multi-block preview branch, and only for a target other than the currently
	 * previewed one. A single-target blitz never hits it (usingMultiBlock is false), which is why
	 * the blitz path above always includes the assist.
	 */
	@Test
	void multiBlockPreviewOmitsAssistForOtherTargetButBlitzNeverDoes() {
		Team kickingTeam = mock(Team.class);
		when(kickingTeam.getId()).thenReturn(KICKING_TEAM_ID);

		Player<?> attacker = mockPlayer(3);
		Player<?> firstTarget = mockPlayer(3);
		Player<?> secondTarget = mockPlayer(3);
		when(firstTarget.getId()).thenReturn("first");
		when(secondTarget.getId()).thenReturn("second");

		// during multi-block preview only the first target is selected
		Set<String> multiBlockTargets = new HashSet<>(Collections.singletonList("first"));
		Game game = mockBlitzGame(kickingTeam, multiBlockTargets);
		GameState gameState = mock(GameState.class);
		when(gameState.getGame()).thenReturn(game);
		when(gameState.getAdditionalAssist(KICKING_TEAM_ID)).thenReturn(1);

		try (MockedStatic<ServerUtilPlayer> util = mockStatic(ServerUtilPlayer.class)) {
			// multi-block applies a -2 attacker modifier, so the base strength passed on is 3 - 2 = 1
			util.when(() -> ServerUtilPlayer.findBlockStrength(eq(game), eq(attacker), eq(1), any(), eq(true)))
				.thenReturn(1);

			int firstTargetStrength =
				mechanic().getTotalAttackerStrength(gameState, attacker, firstTarget, true, false, false, 3);
			int secondTargetStrength =
				mechanic().getTotalAttackerStrength(gameState, attacker, secondTarget, true, false, false, 3);

			assertEquals(2, firstTargetStrength, "Multi-block preview keeps the assist on the selected target");
			assertEquals(1, secondTargetStrength, "Multi-block preview omits the assist on the not-yet-selected target");
		}
	}

	/**
	 * Reproduces the turn bookkeeping of the reported scenario: the kicking (defending) team wins
	 * Cheering Fans, then the receiving team plays and ends its regular turn. The removal in
	 * StepEndTurn/StepBlockRoll always targets {@code getActingTeam()}, i.e. the receiving team, so
	 * the kicking team's assist must still be present when its own first (blitz) block is computed.
	 */
	@Test
	void kickingTeamAssistSurvivesReceivingTeamTurn() {
		ActiveEffects effects = new ActiveEffects();
		effects.setTeamIdsAdditionalAssist(new HashSet<>(Collections.singletonList(KICKING_TEAM_ID)));

		// receiving team makes a block during its turn -> removal targets the acting (receiving) team
		effects.removeAdditionalAssist(RECEIVING_TEAM_ID);
		// receiving team ends its regular turn -> removal again targets the receiving team
		effects.removeAdditionalAssist(RECEIVING_TEAM_ID);

		assertTrue(effects.getTeamIdsAdditionalAssist().contains(KICKING_TEAM_ID),
			"The kicking team's Cheering-Fans assist must survive the receiving team's turn");
	}
}

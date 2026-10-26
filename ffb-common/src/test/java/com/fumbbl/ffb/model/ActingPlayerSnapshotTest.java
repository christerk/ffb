package com.fumbbl.ffb.model;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActingPlayerSnapshotTest {
	private final ConsummateProfessional skill = new ConsummateProfessional();
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		skill.postConstruct();
		Game game = mock(Game.class);
		Player<?> blitzer = mock(Player.class);
		Player<?> passBlocker = mock(Player.class);
		when(blitzer.getStrengthWithModifiers()).thenReturn(3);
		when(game.getPlayerById("blitzer")).thenAnswer(invocation -> blitzer);
		when(game.getPlayerById("passBlocker")).thenAnswer(invocation -> passBlocker);
		actingPlayer = new ActingPlayer(game);
	}

	@Test
	void restoreFromSnapshotContinuesInterruptedAction() {
		actingPlayer.setPlayerId("blitzer");
		actingPlayer.setPlayerAction(PlayerAction.BLITZ);
		actingPlayer.setCurrentMove(4);
		actingPlayer.setGoingForIt(true);
		actingPlayer.setHasBlocked(true);
		actingPlayer.setHasMoved(true);
		actingPlayer.setOldPlayerState(new PlayerState(PlayerState.STANDING));
		actingPlayer.setInitialAdjacentPartnerIds(new String[]{"partner"});
		actingPlayer.markSkillUsed(skill);

		ActingPlayer snapshot = actingPlayer.snapshot();

		actingPlayer.setPlayerId("passBlocker");
		actingPlayer.setPlayerAction(PlayerAction.MOVE);
		actingPlayer.setCurrentMove(2);
		assertFalse(actingPlayer.hasBlocked());
		assertNull(actingPlayer.getOldPlayerState());

		actingPlayer.restoreFrom(snapshot);

		assertEquals("blitzer", actingPlayer.getPlayerId());
		assertEquals(PlayerAction.BLITZ, actingPlayer.getPlayerAction());
		assertEquals(3, actingPlayer.getStrength());
		assertEquals(4, actingPlayer.getCurrentMove());
		assertTrue(actingPlayer.isGoingForIt());
		assertTrue(actingPlayer.hasBlocked());
		assertTrue(actingPlayer.hasMoved());
		assertEquals(PlayerState.STANDING, actingPlayer.getOldPlayerState().getBase());
		assertArrayEquals(new String[]{"partner"}, actingPlayer.getInitialAdjacentPartnerIds());
		assertTrue(actingPlayer.isSkillUsed(skill));
	}

	@Test
	void snapshotIsNotAffectedByLaterChanges() {
		actingPlayer.setPlayerId("blitzer");
		actingPlayer.setCurrentMove(1);

		ActingPlayer snapshot = actingPlayer.snapshot();
		actingPlayer.setCurrentMove(3);
		actingPlayer.markSkillUsed(skill);

		assertEquals(1, snapshot.getCurrentMove());
		assertEquals(0, snapshot.getUsedSkills().length);
	}
}

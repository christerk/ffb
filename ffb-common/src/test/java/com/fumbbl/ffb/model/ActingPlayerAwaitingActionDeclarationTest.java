package com.fumbbl.ffb.model;

import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActingPlayerAwaitingActionDeclarationTest {
	private Game game;
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		Player<?> squig = mock(Player.class);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		actingPlayer = new ActingPlayer(game);
		actingPlayer.setPlayerId("squig");
	}

	@Test
	void settingTheFlagNotifiesObservers() {
		actingPlayer.setAwaitingActionDeclaration(true);

		assertTrue(actingPlayer.isAwaitingActionDeclaration());
		ArgumentCaptor<ModelChange> captor = ArgumentCaptor.forClass(ModelChange.class);
		verify(game, atLeastOnce()).notifyObservers(captor.capture());
		ModelChange change = captor.getValue();
		assertEquals(ModelChangeId.ACTING_PLAYER_SET_AWAITING_ACTION_DECLARATION, change.getChangeId());
		assertEquals(true, change.getValue());
	}

	@Test
	void changingThePlayerResetsTheFlag() {
		actingPlayer.setAwaitingActionDeclaration(true);

		actingPlayer.setPlayerId(null);

		assertFalse(actingPlayer.isAwaitingActionDeclaration());
	}

	@Test
	void snapshotRestoresTheFlag() {
		actingPlayer.setAwaitingActionDeclaration(true);
		ActingPlayer snapshot = actingPlayer.snapshot();
		actingPlayer.setAwaitingActionDeclaration(false);

		actingPlayer.restoreFrom(snapshot);

		assertTrue(actingPlayer.isAwaitingActionDeclaration());
	}
}

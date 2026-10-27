package com.fumbbl.ffb.util.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TurnActionLimitServiceTest {
	private final TurnActionLimitService service = new TurnActionLimitService();
	private Game game;
	private TurnData turnData;
	private ActingPlayer actingPlayer;
	private Player<?> player;
	private Player<?> otherPlayer;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		turnData = mock(TurnData.class);
		actingPlayer = mock(ActingPlayer.class);
		player = mock(Player.class);
		otherPlayer = mock(Player.class);
		when(player.getId()).thenReturn("player");
		when(otherPlayer.getId()).thenReturn("otherPlayer");
		when(game.getTurnData()).thenReturn(turnData);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
	}

	@Test
	void blitzActionsAreRejectedWhenBlitzIsUsed() {
		when(turnData.isBlitzUsed()).thenReturn(true);

		assertFalse(service.isActionAvailable(game, player, PlayerAction.BLITZ_MOVE));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.BLITZ));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.KICK_EM_BLITZ));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.STAND_UP_BLITZ));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.FURIOUS_OUTPBURST));
	}

	@Test
	void blitzIsAvailableWhenNotUsed() {
		assertTrue(service.isActionAvailable(game, player, PlayerAction.BLITZ_MOVE));
	}

	@Test
	void anotherPlayerCannotBlitzWhileBlitzerIsActing() {
		when(turnData.isBlitzUsed()).thenReturn(true);
		when(actingPlayer.getPlayerId()).thenReturn("player");
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLITZ_MOVE);

		assertFalse(service.isActionAvailable(game, otherPlayer, PlayerAction.BLITZ_MOVE));
	}

	@Test
	void actingPlayerCanContinueDeclaredAction() {
		when(turnData.isBlitzUsed()).thenReturn(true);
		when(turnData.isPassUsed()).thenReturn(true);
		when(actingPlayer.getPlayerId()).thenReturn("player");

		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLITZ);
		assertTrue(service.isActionAvailable(game, player, PlayerAction.BLITZ_MOVE));

		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.PASS_MOVE);
		assertTrue(service.isActionAvailable(game, player, PlayerAction.PASS));
	}

	@Test
	void actingPlayerCannotSwitchToUsedAction() {
		when(turnData.isPassUsed()).thenReturn(true);
		when(actingPlayer.getPlayerId()).thenReturn("player");
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.MOVE);

		assertFalse(service.isActionAvailable(game, player, PlayerAction.PASS));
	}

	@Test
	void otherLimitedActionsAreRejectedWhenUsed() {
		when(turnData.isPassUsed()).thenReturn(true);
		when(turnData.isHandOverUsed()).thenReturn(true);
		when(turnData.isTtmUsed()).thenReturn(true);
		when(turnData.isKtmUsed()).thenReturn(true);
		when(turnData.isBombUsed()).thenReturn(true);
		when(turnData.isSecureTheBallUsed()).thenReturn(true);
		when(turnData.isPuntUsed()).thenReturn(true);

		assertFalse(service.isActionAvailable(game, player, PlayerAction.PASS_MOVE));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.HAIL_MARY_PASS));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.HAND_OVER_MOVE));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.THROW_TEAM_MATE_MOVE));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.KICK_TEAM_MATE));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.THROW_BOMB));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.ALL_YOU_CAN_EAT));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.SECURE_THE_BALL));
		assertFalse(service.isActionAvailable(game, player, PlayerAction.PUNT_MOVE));
	}

	@Test
	void foulIsAvailableAgainForPlayerAllowedAdditionalFoul() {
		when(turnData.isFoulUsed()).thenReturn(true);
		when(otherPlayer.hasSkillProperty(NamedProperties.allowsAdditionalFoul)).thenReturn(true);

		assertFalse(service.isActionAvailable(game, player, PlayerAction.FOUL_MOVE));
		assertTrue(service.isActionAvailable(game, otherPlayer, PlayerAction.FOUL_MOVE));
	}

	@Test
	void unlimitedActionsAreAlwaysAvailable() {
		when(turnData.isBlitzUsed()).thenReturn(true);

		assertTrue(service.isActionAvailable(game, player, PlayerAction.MOVE));
		assertTrue(service.isActionAvailable(game, player, PlayerAction.BLOCK));
		assertTrue(service.isActionAvailable(game, player, null));
	}
}

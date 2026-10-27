package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PassMechanicTest {

	private static final FieldCoordinate THROWER_COORDINATE = new FieldCoordinate(5, 5);
	private static final FieldCoordinate BLOCKER_COORDINATE = new FieldCoordinate(4, 5);
	private static final FieldCoordinate MARKER_COORDINATE = new FieldCoordinate(6, 5);

	private final PassMechanic mechanic = new PassMechanic();

	private Game game;
	private Player<?> thrower;
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		Team homeTeam = mock(Team.class);
		Team awayTeam = mock(Team.class);

		thrower = mockPlayer("thrower", awayTeam);
		Player<?> blocker = mockPlayer("blocker", homeTeam);
		Player<?> marker = mockPlayer("marker", homeTeam);

		FieldModel fieldModel = mock(FieldModel.class);
		doCallRealMethod().when(fieldModel)
			.findAdjacentCoordinates(any(FieldCoordinate.class), any(FieldCoordinateBounds.class), anyInt(), anyBoolean());
		when(fieldModel.getPlayerCoordinate(thrower)).thenReturn(THROWER_COORDINATE);
		doReturn(blocker).when(fieldModel).getPlayer(BLOCKER_COORDINATE);
		doReturn(marker).when(fieldModel).getPlayer(MARKER_COORDINATE);
		when(fieldModel.getPlayerState(blocker)).thenReturn(new PlayerState(PlayerState.MOVING));
		when(fieldModel.getPlayerState(marker)).thenReturn(new PlayerState(PlayerState.STANDING));

		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayerId()).thenReturn("blocker");

		game = mock(Game.class);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getTeamHome()).thenReturn(homeTeam);
		when(game.getTeamAway()).thenReturn(awayTeam);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.getTurnMode()).thenReturn(TurnMode.DUMP_OFF);
	}

	@Test
	void dumpOffCountsTacklezoneOfBlockerWhoStoodUp() {
		when(actingPlayer.isStandingUp()).thenReturn(true);

		assertEquals(2, mechanic.passModifiers(game, thrower));
	}

	@Test
	void dumpOffCountsTacklezoneOfStandingBlocker() {
		when(actingPlayer.isStandingUp()).thenReturn(false);

		assertEquals(2, mechanic.passModifiers(game, thrower));
	}

	private Player<?> mockPlayer(String id, Team team) {
		Player<?> player = mock(Player.class);
		when(player.getId()).thenReturn(id);
		when(player.getTeam()).thenReturn(team);
		return player;
	}
}

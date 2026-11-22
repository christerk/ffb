package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilPlayerFindStandingPlayersTest {
	private static final FieldCoordinate CENTER = new FieldCoordinate(10, 7);

	private Game game;
	private FieldModel fieldModel;
	private Team team;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		fieldModel = new FieldModel(game);
		when(game.getFieldModel()).thenReturn(fieldModel);
		team = mock(Team.class);
		place("center", team, CENTER, PlayerState.STANDING);
	}

	@Test
	void findsStandingMovingAndBlockedTeamMatesWithinDistance() {
		Player<?> standing = place("standing", team, new FieldCoordinate(13, 7), PlayerState.STANDING);
		Player<?> moving = place("moving", team, new FieldCoordinate(11, 8), PlayerState.MOVING);
		Player<?> blocked = place("blocked", team, new FieldCoordinate(7, 4), PlayerState.BLOCKED);

		assertContainsExactly(new Player<?>[]{standing, moving, blocked},
			UtilPlayer.findStandingPlayers(game, team, CENTER, 3));
	}

	@Test
	void ignoresProneAndStunnedPlayers() {
		place("prone", team, new FieldCoordinate(11, 7), PlayerState.PRONE);
		place("stunned", team, new FieldCoordinate(9, 7), PlayerState.STUNNED);

		assertArrayEquals(new Player<?>[0], UtilPlayer.findStandingPlayers(game, team, CENTER, 3));
	}

	@Test
	void ignoresPlayersFurtherAwayOpponentsAndThePlayerAtTheCenter() {
		place("far", team, new FieldCoordinate(14, 7), PlayerState.STANDING);
		place("opponent", mock(Team.class), new FieldCoordinate(11, 7), PlayerState.STANDING);

		Player<?>[] found = UtilPlayer.findStandingPlayers(game, team, CENTER, 3);

		assertArrayEquals(new Player<?>[0], found);
	}

	private Player<?> place(String id, Team playerTeam, FieldCoordinate coordinate, int base) {
		Player<?> player = mock(Player.class);
		when(player.getId()).thenReturn(id);
		when(player.getTeam()).thenReturn(playerTeam);
		when(game.getPlayerById(id)).thenAnswer(invocation -> player);
		fieldModel.setPlayerCoordinate(player, coordinate);
		fieldModel.setPlayerState(player, new PlayerState(base).changeActive(true));
		return player;
	}

	private void assertContainsExactly(Player<?>[] expected, Player<?>[] actual) {
		assertArrayEquals(ids(expected), ids(actual));
	}

	private String[] ids(Player<?>[] players) {
		return Arrays.stream(players).map(Player::getId).sorted().toArray(String[]::new);
	}
}

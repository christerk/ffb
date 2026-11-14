package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedingVictimServiceTest {

	private static final FieldCoordinate VAMPIRE = new FieldCoordinate(5, 5);

	private final FeedingVictimService service = new FeedingVictimService();
	private final List<FieldCoordinate> adjacentCoordinates = new ArrayList<>();

	private Game game;
	private FieldModel fieldModel;
	private Team team;
	private Team opponentTeam;

	@BeforeEach
	void setUp() {
		team = mock(Team.class);
		opponentTeam = mock(Team.class);
		fieldModel = mock(FieldModel.class);
		game = mock(Game.class);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(fieldModel.findAdjacentCoordinates(eq(VAMPIRE), any(FieldCoordinateBounds.class), anyInt(), anyBoolean()))
			.then(invocation -> adjacentCoordinates.toArray(new FieldCoordinate[0]));
	}

	@Test
	void offersOnlyOpponentsWithStrengthThreeOrLower() {
		Player<?> strengthTwo = place(opponent(2, PlayerType.REGULAR), 4, 4);
		Player<?> strengthThree = place(opponent(3, PlayerType.REGULAR), 4, 5);
		place(opponent(4, PlayerType.REGULAR), 4, 6);
		place(opponent(5, PlayerType.BIG_GUY), 5, 4);

		assertEquals(Arrays.asList(strengthTwo, strengthThree), service.findVictims(game, team, VAMPIRE, true));
	}

	@Test
	void doesNotOfferOpposingStarPlayers() {
		place(opponent(3, PlayerType.STAR), 4, 4);

		assertTrue(service.findVictims(game, team, VAMPIRE, true).isEmpty());
	}

	@Test
	void offersOnlyThrallsWithoutTastyMorsel() {
		Player<?> thrall = place(teamMate(true), 4, 4);
		place(teamMate(false), 4, 5);
		place(opponent(2, PlayerType.REGULAR), 4, 6);

		assertEquals(Arrays.asList(thrall), service.findVictims(game, team, VAMPIRE, false));
	}

	@Test
	void offersThrallsAndEligibleOpponentsWithTastyMorsel() {
		Player<?> thrall = place(teamMate(true), 4, 4);
		Player<?> opponent = place(opponent(3, PlayerType.REGULAR), 4, 5);

		assertEquals(Arrays.asList(thrall, opponent), service.findVictims(game, team, VAMPIRE, true));
	}

	private Player<?> place(Player<?> player, int x, int y) {
		FieldCoordinate coordinate = new FieldCoordinate(x, y);
		adjacentCoordinates.add(coordinate);
		when(fieldModel.getPlayer(coordinate)).then(invocation -> player);
		return player;
	}

	private Player<?> opponent(int strength, PlayerType type) {
		RosterPosition position = mock(RosterPosition.class);
		when(position.getType()).thenReturn(type);
		RosterPlayer player = mock(RosterPlayer.class);
		when(player.getTeam()).thenReturn(opponentTeam);
		when(player.getPosition()).thenReturn(position);
		when(player.getStrengthWithModifiers()).thenReturn(strength);
		return player;
	}

	private Player<?> teamMate(boolean thrall) {
		RosterPosition position = mock(RosterPosition.class);
		when(position.isThrall()).thenReturn(thrall);
		RosterPlayer player = mock(RosterPlayer.class);
		when(player.getTeam()).thenReturn(team);
		when(player.getPosition()).thenReturn(position);
		return player;
	}
}

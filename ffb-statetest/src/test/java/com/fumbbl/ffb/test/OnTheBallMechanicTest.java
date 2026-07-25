package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.mixed.OnTheBallMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OnTheBallMechanicTest {

	private OnTheBallMechanic mechanic;

	@BeforeEach
	public void setUp() {
		mechanic = new OnTheBallMechanic();
	}

	@Test
	public void getTypeReturnsOnTheBall() {
		assertEquals(Mechanic.Type.ON_THE_BALL, mechanic.getType());
	}

	@Test
	public void findPassBlockersReturnsPlayersWithSkillAndTacklezones() {
		Game game = mock(Game.class, withSettings().lenient());
		FieldModel fieldModel = mock(FieldModel.class);
		when(game.getFieldModel()).thenReturn(fieldModel);

		Team team = mock(Team.class);
		Player<?> playerWithSkill = mock(Player.class);
		when(playerWithSkill.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)).thenReturn(true);
		when(team.getPlayers()).thenReturn(new Player[]{playerWithSkill});

		PlayerState standingActive = new PlayerState(PlayerState.STANDING).changeActive(true);
		when(fieldModel.getPlayerState(playerWithSkill)).thenReturn(standingActive);

		Set<Player<?>> blockers = mechanic.findPassBlockers(game, team, false);

		assertEquals(1, blockers.size());
		assertTrue(blockers.contains(playerWithSkill));
	}

	@Test
	public void findPassBlockersExcludesPlayersWithoutTacklezones() {
		Game game = mock(Game.class, withSettings().lenient());
		FieldModel fieldModel = mock(FieldModel.class);
		when(game.getFieldModel()).thenReturn(fieldModel);

		Team team = mock(Team.class);
		Player<?> pronePlayer = mock(Player.class);
		when(pronePlayer.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)).thenReturn(true);
		when(team.getPlayers()).thenReturn(new Player[]{pronePlayer});

		PlayerState proneState = new PlayerState(PlayerState.PRONE).changeActive(true);
		when(fieldModel.getPlayerState(pronePlayer)).thenReturn(proneState);

		Set<Player<?>> blockers = mechanic.findPassBlockers(game, team, false);

		assertTrue(blockers.isEmpty());
	}

	@Test
	public void findPassBlockersExcludesPlayersWithoutSkill() {
		Game game = mock(Game.class, withSettings().lenient());
		FieldModel fieldModel = mock(FieldModel.class);
		when(game.getFieldModel()).thenReturn(fieldModel);

		Team team = mock(Team.class);
		Player<?> playerWithoutSkill = mock(Player.class);
		when(playerWithoutSkill.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)).thenReturn(false);
		when(team.getPlayers()).thenReturn(new Player[]{playerWithoutSkill});

		PlayerState standingActive = new PlayerState(PlayerState.STANDING).changeActive(true);
		when(fieldModel.getPlayerState(playerWithoutSkill)).thenReturn(standingActive);

		Set<Player<?>> blockers = mechanic.findPassBlockers(game, team, false);

		assertTrue(blockers.isEmpty());
	}

	@Test
	public void findPassBlockersExcludesConfusedPlayers() {
		Game game = mock(Game.class, withSettings().lenient());
		FieldModel fieldModel = mock(FieldModel.class);
		when(game.getFieldModel()).thenReturn(fieldModel);

		Team team = mock(Team.class);
		Player<?> confusedPlayer = mock(Player.class);
		when(confusedPlayer.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)).thenReturn(true);
		when(team.getPlayers()).thenReturn(new Player[]{confusedPlayer});

		PlayerState confusedState = new PlayerState(PlayerState.STANDING).changeActive(true).changeConfused(true);
		when(fieldModel.getPlayerState(confusedPlayer)).thenReturn(confusedState);

		Set<Player<?>> blockers = mechanic.findPassBlockers(game, team, false);

		assertTrue(blockers.isEmpty(), "Confused players should not have tacklezones and should be excluded");
	}

	@Test
	public void validPassBlockMoveAllowsUpToThreeSquares() {
		Game game = mock(Game.class, withSettings().lenient());
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getCurrentMove()).thenReturn(0);

		assertTrue(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 3));
		assertTrue(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 2));
		assertTrue(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 1));
		assertTrue(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 0));
	}

	@Test
	public void validPassBlockMoveRejectsOverThreeSquares() {
		Game game = mock(Game.class, withSettings().lenient());
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getCurrentMove()).thenReturn(0);

		assertFalse(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 4));
	}

	@Test
	public void validPassBlockMoveAccountsForCurrentMove() {
		Game game = mock(Game.class, withSettings().lenient());
		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getCurrentMove()).thenReturn(2);

		assertTrue(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 1));
		assertFalse(mechanic.validPassBlockMove(game, actingPlayer, null, null, null, false, 2));
	}

	@Test
	public void hasReachedValidPositionAlwaysReturnsTrue() {
		Game game = mock(Game.class, withSettings().lenient());
		Player<?> anyPlayer = mock(Player.class);
		assertTrue(mechanic.hasReachedValidPosition(game, anyPlayer));
	}

	@Test
	public void displayStringPassInterferenceIsOnTheBall() {
		assertEquals("On The Ball", mechanic.displayStringPassInterference());
	}

	@Test
	public void displayStringKickOffInterferenceIsOnTheBall() {
		assertEquals("On The Ball", mechanic.displayStringKickOffInterference());
	}

	@Test
	public void passInterferenceDialogDescriptionIsCorrect() {
		String[] description = mechanic.passInterferenceDialogDescription();
		assertEquals(1, description.length);
		assertEquals("You may move your players with ON THE BALL skill up to 3 squares.", description[0]);
	}

	@Test
	public void passInterferenceStatusDescriptionIsCorrect() {
		assertEquals("Waiting for coach to move players with \"On The Ball\".", mechanic.passInterferenceStatusDescription());
	}
}

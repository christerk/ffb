package com.fumbbl.ffb.util.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import com.fumbbl.ffb.skill.bb2025.SquigRestraint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SquigRestraintTest {
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);
	private static final FieldCoordinate SQUIG_COORDINATE = new FieldCoordinate(10, 7);

	private final SquigRageService service = new SquigRageService();
	private final SquigRage squigRage = new SquigRage();
	private Game game;
	private FieldModel fieldModel;
	private ActingPlayer actingPlayer;
	private Team team;
	private Team otherTeam;
	private Player<?> squig;
	private Player<?> restrainer;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		SquigRestraint squigRestraint = new SquigRestraint();
		squigRestraint.postConstruct();
		game = mock(Game.class);
		fieldModel = new FieldModel(game);
		actingPlayer = mock(ActingPlayer.class);
		team = mock(Team.class);
		otherTeam = mock(Team.class);
		squig = mock(Player.class);
		restrainer = mock(Player.class);

		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);

		when(squig.getId()).thenReturn("squig");
		when(restrainer.getId()).thenReturn("restrainer");
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(game.getPlayerById("restrainer")).thenAnswer(invocation -> restrainer);
		when(squig.getTeam()).thenReturn(team);
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(restrainer.getTeam()).thenReturn(team);
		when(restrainer.hasSkillProperty(NamedProperties.preventsRageRollOfNearbyTeamMates))
			.thenReturn(squigRestraint.hasSkillProperty(NamedProperties.preventsRageRollOfNearbyTeamMates));
		when(team.getPlayers()).thenReturn(new Player<?>[]{squig, restrainer});
		when(otherTeam.getPlayers()).thenReturn(new Player<?>[0]);

		fieldModel.setPlayerCoordinate(squig, SQUIG_COORDINATE);
		fieldModel.setPlayerState(squig, STANDING);
		givenRestrainer(new FieldCoordinate(13, 7), STANDING);
	}

	@Test
	void standingRestrainerWithinThreeSquaresPreventsRoll() {
		assertTrue(service.isRestrained(game, squig));
		assertFalse(service.requiresRoll(game, squig));
	}

	@Test
	void diagonalDistanceCountsInSquares() {
		givenRestrainer(new FieldCoordinate(13, 10), STANDING);

		assertFalse(service.requiresRoll(game, squig));
	}

	@Test
	void restrainerFurtherAwayDoesNotPreventRoll() {
		givenRestrainer(new FieldCoordinate(14, 7), STANDING);

		assertFalse(service.isRestrained(game, squig));
		assertTrue(service.requiresRoll(game, squig));
	}

	@Test
	void proneOrStunnedRestrainerDoesNotPreventRoll() {
		givenRestrainer(new FieldCoordinate(11, 7), new PlayerState(PlayerState.PRONE).changeActive(true));
		assertTrue(service.requiresRoll(game, squig));

		givenRestrainer(new FieldCoordinate(11, 7), new PlayerState(PlayerState.STUNNED));
		assertTrue(service.requiresRoll(game, squig));
	}

	@Test
	void movingRestrainerIsStanding() {
		givenRestrainer(new FieldCoordinate(11, 7), new PlayerState(PlayerState.MOVING).changeActive(true));

		assertFalse(service.requiresRoll(game, squig));
	}

	@Test
	void restrainerOffThePitchDoesNotPreventRoll() {
		givenRestrainer(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 7), new PlayerState(PlayerState.RESERVE));

		assertTrue(service.requiresRoll(game, squig));
	}

	@Test
	void opposingRestrainerDoesNotPreventRoll() {
		when(team.getPlayers()).thenReturn(new Player<?>[]{squig});
		when(otherTeam.getPlayers()).thenReturn(new Player<?>[]{restrainer});
		when(restrainer.getTeam()).thenReturn(otherTeam);

		assertTrue(service.requiresRoll(game, squig));
	}

	@Test
	void playerDoesNotRestrainThemself() {
		when(squig.hasSkillProperty(NamedProperties.preventsRageRollOfNearbyTeamMates)).thenReturn(true);
		when(team.getPlayers()).thenReturn(new Player<?>[]{squig});
		fieldModel.remove(restrainer);

		assertTrue(service.requiresRoll(game, squig));
	}

	@Test
	void playerActivatedWhileRestrainedDoesNotRollLater() {
		when(actingPlayer.getPlayerId()).thenReturn("squig");
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.MOVE);
		givenRestrainer(new FieldCoordinate(20, 7), STANDING);

		assertFalse(service.requiresRoll(game, squig));
	}

	private void givenRestrainer(FieldCoordinate coordinate, PlayerState state) {
		fieldModel.setPlayerCoordinate(restrainer, coordinate);
		fieldModel.setPlayerState(restrainer, state);
	}
}

package com.fumbbl.ffb.util.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SquigRageServiceTest {
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);
	private static final PlayerState PRONE = new PlayerState(PlayerState.PRONE).changeActive(true);

	private final SquigRageService service = new SquigRageService();
	private Game game;
	private FieldModel fieldModel;
	private ActingPlayer actingPlayer;
	private GameMechanic gameMechanic;
	private Skill squigRage;
	private Player<?> squig;
	private Player<?> opponent;
	private Player<?> teamMate;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		fieldModel = mock(FieldModel.class);
		actingPlayer = mock(ActingPlayer.class);
		gameMechanic = mock(GameMechanic.class);
		squigRage = mock(Skill.class);
		Team team = mock(Team.class);
		Team otherTeam = mock(Team.class);
		squig = mock(Player.class);
		opponent = mock(Player.class);
		teamMate = mock(Player.class);

		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		doReturn(gameMechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(gameMechanic.isBlockActionAllowed(TurnMode.REGULAR)).thenReturn(true);
		when(game.getOtherTeam(team)).thenReturn(otherTeam);

		when(squig.getId()).thenReturn("squig");
		when(squig.getTeam()).thenReturn(team);
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(opponent.getTeam()).thenReturn(otherTeam);
		when(teamMate.getTeam()).thenReturn(team);
		when(team.getPlayers()).thenReturn(new Player<?>[]{squig, teamMate});

		FieldCoordinate squigCoordinate = new FieldCoordinate(10, 7);
		FieldCoordinate opponentCoordinate = new FieldCoordinate(11, 7);
		FieldCoordinate teamMateCoordinate = new FieldCoordinate(9, 7);
		when(fieldModel.getPlayerCoordinate(squig)).thenReturn(squigCoordinate);
		when(fieldModel.findAdjacentCoordinates(eq(squigCoordinate), eq(FieldCoordinateBounds.FIELD), anyInt(),
			anyBoolean())).thenReturn(new FieldCoordinate[]{opponentCoordinate, teamMateCoordinate});
		doReturn(opponent).when(fieldModel).getPlayer(opponentCoordinate);
		doReturn(teamMate).when(fieldModel).getPlayer(teamMateCoordinate);
		when(fieldModel.getPlayerState(any())).thenReturn(STANDING);
	}

	@Test
	void requiresRollUntilSkillIsUsedByActingPlayer() {
		assertTrue(service.requiresRoll(game, squig));

		when(actingPlayer.getPlayerId()).thenReturn("squig");
		assertTrue(service.requiresRoll(game, squig));

		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		assertFalse(service.requiresRoll(game, squig));
	}

	@Test
	void noRollOutsideOfRegularActivations() {
		when(game.getTurnMode()).thenReturn(TurnMode.KICKOFF_RETURN);

		assertFalse(service.requiresRoll(game, squig));
	}

	@Test
	void noRollWithoutSkill() {
		assertFalse(service.requiresRoll(game, teamMate));
	}

	@Test
	void standingPlayerMustBlockMarkedOpponent() {
		assertArrayEquals(new Player<?>[]{opponent}, service.blockTargets(game, squig));
	}

	@Test
	void ballAndChainDoesNotPreventBlock() {
		when(squig.hasSkillProperty(NamedProperties.preventRegularBlockAction)).thenReturn(true);

		assertArrayEquals(new Player<?>[]{opponent}, service.blockTargets(game, squig));
	}

	@Test
	void playerWithoutTacklezonesMustStillBlock() {
		when(fieldModel.getPlayerState(squig)).thenReturn(STANDING.changeConfused(true));

		assertArrayEquals(new Player<?>[]{opponent}, service.blockTargets(game, squig));
	}

	@Test
	void pronePlayerBlocksOnlyWithJumpUp() {
		when(fieldModel.getPlayerState(squig)).thenReturn(PRONE);
		assertEquals(0, service.blockTargets(game, squig).length);

		when(squig.hasSkillProperty(NamedProperties.canStandUpForFree)).thenReturn(true);
		assertArrayEquals(new Player<?>[]{opponent}, service.blockTargets(game, squig));
	}

	@Test
	void standingUpActingPlayerIsTreatedAsProne() {
		when(actingPlayer.getPlayerId()).thenReturn("squig");
		when(actingPlayer.isStandingUp()).thenReturn(true);

		assertEquals(0, service.blockTargets(game, squig).length);
	}

	@Test
	void noBlockWhenBlockActionIsNotAllowed() {
		when(gameMechanic.isBlockActionAllowed(TurnMode.REGULAR)).thenReturn(false);

		assertEquals(0, service.blockTargets(game, squig).length);
	}

	@Test
	void noBlockAgainstProneOpponent() {
		when(fieldModel.getPlayerState(opponent)).thenReturn(PRONE);

		assertEquals(0, service.blockTargets(game, squig).length);
	}

	@Test
	void standingAdjacentTeamMatesCanBeKnockedDown() {
		assertArrayEquals(new Player<?>[]{teamMate}, service.teamMateTargets(game, squig));

		when(fieldModel.getPlayerState(teamMate)).thenReturn(PRONE);
		assertEquals(0, service.teamMateTargets(game, squig).length);
	}

	@Test
	void hasRolledOnceSkillIsUsed() {
		doReturn(squig).when(actingPlayer).getPlayer();
		assertFalse(service.hasRolled(actingPlayer));

		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		assertTrue(service.hasRolled(actingPlayer));
	}

	@Test
	void playerWithoutSkillHasNotRolled() {
		doReturn(teamMate).when(actingPlayer).getPlayer();

		assertFalse(service.hasRolled(actingPlayer));
	}

	@Test
	void passedRollAwaitsActionDeclarationAndBlocksOtherPlayers() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.SQUIG_RAGE);
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		when(actingPlayer.isAwaitingActionDeclaration()).thenReturn(true);

		assertTrue(service.isAwaitingActionDeclaration(actingPlayer));
		assertTrue(service.isActivationBlocked(actingPlayer, teamMate));
		assertFalse(service.isActivationBlocked(actingPlayer, squig));
	}

	@Test
	void rolledPlayerDoesNotAwaitDeclarationWhileSequenceIsUnresolved() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.SQUIG_RAGE);
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);

		assertFalse(service.isAwaitingActionDeclaration(actingPlayer));
		assertFalse(service.isActivationBlocked(actingPlayer, teamMate));
	}

	@Test
	void otherPlayersAreNotBlockedBeforeRollOrOnceActionIsDeclared() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.SQUIG_RAGE);
		assertFalse(service.isActivationBlocked(actingPlayer, teamMate));

		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		when(actingPlayer.isAwaitingActionDeclaration()).thenReturn(true);
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.MOVE);
		assertFalse(service.isAwaitingActionDeclaration(actingPlayer));
		assertFalse(service.isActivationBlocked(actingPlayer, teamMate));
	}

	@Test
	void noActingPlayerBlocksNobody() {
		assertFalse(service.isActivationBlocked(actingPlayer, teamMate));
	}

	@Test
	void rollAloneDoesNotCountAsActing() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.hasActed()).thenReturn(true);
		when(actingPlayer.hasActedIgnoring(squigRage)).thenReturn(false);

		assertFalse(service.hasActedApartFromRoll(actingPlayer));

		when(actingPlayer.hasActedIgnoring(squigRage)).thenReturn(true);
		assertTrue(service.hasActedApartFromRoll(actingPlayer));
	}

	@Test
	void actingWithoutSkillIsRegularActing() {
		doReturn(teamMate).when(actingPlayer).getPlayer();
		when(actingPlayer.hasActed()).thenReturn(true);

		assertTrue(service.hasActedApartFromRoll(actingPlayer));
	}

	@Test
	void proneRolledPlayerWhoDidNothingElseStaysProne() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		when(actingPlayer.isStandingUp()).thenReturn(true);

		assertTrue(service.isOnlyActivatedWhileProne(actingPlayer));
	}

	@Test
	void proneRolledPlayerWhoActedDoesNotStayProne() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		when(actingPlayer.isStandingUp()).thenReturn(true);
		when(actingPlayer.hasActedIgnoringNegativeTraits()).thenReturn(true);

		assertFalse(service.isOnlyActivatedWhileProne(actingPlayer));
	}

	@Test
	void standingOrNotRolledPlayerDoesNotStayProne() {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
		assertFalse(service.isOnlyActivatedWhileProne(actingPlayer));

		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(false);
		when(actingPlayer.isStandingUp()).thenReturn(true);
		assertFalse(service.isOnlyActivatedWhileProne(actingPlayer));
	}

	@Test
	void blockIsForcedUntilPerformed() {
		when(actingPlayer.isMustCompleteAction()).thenReturn(true);
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLOCK);

		assertTrue(service.isBlockForced(actingPlayer));

		when(actingPlayer.hasBlocked()).thenReturn(true);
		assertFalse(service.isBlockForced(actingPlayer));
	}

	@Test
	void blockIsNotForcedWithoutMustCompleteOrForOtherActions() {
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.BLOCK);
		assertFalse(service.isBlockForced(actingPlayer));

		when(actingPlayer.isMustCompleteAction()).thenReturn(true);
		when(actingPlayer.getPlayerAction()).thenReturn(PlayerAction.THROW_BOMB);
		assertFalse(service.isBlockForced(actingPlayer));
	}
	@Test
	void declaredActionMayBeChangedAfterTheRollUntilThePlayerActs() {
		givenActionDeclaredAfterRoll(PlayerAction.MOVE);

		assertTrue(service.canChangeDeclaredAction(actingPlayer));

		when(actingPlayer.hasActedIgnoring(squigRage)).thenReturn(true);
		assertFalse(service.canChangeDeclaredAction(actingPlayer));
	}

	@Test
	void declaredActionMayNotBeChangedWithoutRollOrWhenForcedToBlock() {
		givenActionDeclaredAfterRoll(PlayerAction.BLOCK);
		when(actingPlayer.isMustCompleteAction()).thenReturn(true);
		assertFalse(service.canChangeDeclaredAction(actingPlayer));

		when(actingPlayer.isMustCompleteAction()).thenReturn(false);
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(false);
		assertFalse(service.canChangeDeclaredAction(actingPlayer));
	}

	@Test
	void activationWithoutDeclaredActionMayNotBeChanged() {
		givenActionDeclaredAfterRoll(PlayerAction.SQUIG_RAGE);
		assertFalse(service.canChangeDeclaredAction(actingPlayer));
	}

	@Test
	void actionDispatchedRightAwayMayBeChangedUntilThePlayerActs() {
		givenActionDeclaredAfterRoll(PlayerAction.PUNT);
		assertTrue(service.canChangeDeclaredAction(actingPlayer));
	}

	private void givenActionDeclaredAfterRoll(PlayerAction playerAction) {
		doReturn(squig).when(actingPlayer).getPlayer();
		when(actingPlayer.getPlayerAction()).thenReturn(playerAction);
		when(actingPlayer.isSkillUsed(squigRage)).thenReturn(true);
	}
}

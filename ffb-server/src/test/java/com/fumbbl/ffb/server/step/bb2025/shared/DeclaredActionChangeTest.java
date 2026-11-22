package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.mixed.pass.state.PassState;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeclaredActionChangeTest {
	private static final PlayerState PRONE = new PlayerState(PlayerState.PRONE).changeActive(true);
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);

	private final SquigRage squigRage = new SquigRage();
	private final Map<Player<?>, PlayerState> states = new HashMap<>();
	private final DeclaredActionChange change = new DeclaredActionChange();
	private GameState gameState;
	private final PassState passState = mock(PassState.class);
	private Game game;
	private FieldModel fieldModel;
	private TurnData turnData;
	private ActingPlayer actingPlayer;
	private Player<?> squig;
	private Player<?> target;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		gameState = mock(GameState.class);
		game = mock(Game.class);
		fieldModel = mock(FieldModel.class);
		turnData = mock(TurnData.class);
		GameMechanic mechanic = mock(GameMechanic.class);
		squig = mock(Player.class);
		target = mock(Player.class);

		when(gameState.getGame()).thenReturn(game);
		when(gameState.getPassState()).thenReturn(passState);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getTurnData()).thenReturn(turnData);
		doReturn(mechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(mechanic.isActivationWithoutDeclaredAction(PlayerAction.SQUIG_RAGE)).thenReturn(true);

		when(squig.getId()).thenReturn("squig");
		when(target.getId()).thenReturn("target");
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(game.getPlayerById("target")).thenAnswer(invocation -> target);

		when(fieldModel.getPlayerState(any())).thenAnswer(invocation -> states.get(invocation.<Player<?>>getArgument(0)));
		doAnswer(invocation -> states.put(invocation.getArgument(0), invocation.getArgument(1)))
			.when(fieldModel).setPlayerState(any(), any());

		actingPlayer = new ActingPlayer(game);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		actingPlayer.setPlayerId("squig");
		actingPlayer.setPlayerAction(PlayerAction.SQUIG_RAGE);
		actingPlayer.markSkillUsed(squigRage);
	}

	@Test
	void changeIsRequestedBySelectingSquigRageForTheActingPlayer() {
		givenDeclaredAction(PlayerAction.MOVE, STANDING);

		assertTrue(change.isRequested(game, new ClientCommandActingPlayer("squig", PlayerAction.SQUIG_RAGE, false)));
		assertFalse(change.isRequested(game, new ClientCommandActingPlayer("squig", PlayerAction.BLITZ_MOVE, false)));
		assertFalse(change.isRequested(game, new ClientCommandActingPlayer("other", PlayerAction.SQUIG_RAGE, false)));
		assertFalse(change.isRequested(game, new ClientCommandActingPlayer(null, PlayerAction.SQUIG_RAGE, false)));
	}

	@Test
	void changeIsNotPossibleOnceThePlayerActed() {
		givenDeclaredAction(PlayerAction.MOVE, STANDING);
		actingPlayer.setHasMoved(true);

		assertFalse(change.isRequested(game, new ClientCommandActingPlayer("squig", PlayerAction.SQUIG_RAGE, false)));
	}

	@Test
	void withdrawingReturnsPronePlayerToTheActivationBySquigRage() {
		givenDeclaredAction(PlayerAction.MOVE, PRONE);
		actingPlayer.setCurrentMove(3);

		change.withdraw(gameState);

		assertEquals(PlayerAction.SQUIG_RAGE, actingPlayer.getPlayerAction());
		assertTrue(actingPlayer.isAwaitingActionDeclaration());
		assertTrue(actingPlayer.isSkillUsed(squigRage));
		assertTrue(actingPlayer.isStandingUp());
		assertEquals(0, actingPlayer.getCurrentMove());
		assertEquals(PlayerState.PRONE, states.get(squig).getBase());
		assertTrue(states.get(squig).isActive());
		verify(fieldModel).clearMoveSquares();
		verify(gameState).resetStalling();
		verify(passState).reset();
		verify(turnData, never()).setBlitzUsed(false);
	}

	@Test
	void withdrawingBlitzReleasesTheSelectedTarget() {
		givenDeclaredAction(PlayerAction.BLITZ_MOVE, STANDING);
		states.put(target, STANDING.addSelectedBlitzTarget());
		TargetSelectionState targetSelectionState = new TargetSelectionState("target").select();
		when(fieldModel.getTargetSelectionState()).thenReturn(targetSelectionState);

		change.withdraw(gameState);

		assertFalse(states.get(target).isSelectedBlitzTarget());
		assertEquals(PlayerState.STANDING, states.get(squig).getBase());
		verify(turnData).setBlitzUsed(false);
		verify(fieldModel).setTargetSelectionState(null);
	}

	private void givenDeclaredAction(PlayerAction playerAction, PlayerState activationState) {
		actingPlayer.setOldPlayerState(activationState);
		actingPlayer.setStandingUp(activationState.getBase() == PlayerState.PRONE);
		actingPlayer.setPlayerAction(playerAction);
		states.put(squig, activationState.changeBase(PlayerState.MOVING));
	}
}

package com.fumbbl.ffb.util;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilActingPlayerTest {
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);
	private static final PlayerState PRONE = new PlayerState(PlayerState.PRONE).changeActive(true);

	private final SquigRage squigRage = new SquigRage();
	private final Map<Player<?>, PlayerState> states = new HashMap<>();
	private Game game;
	private GameMechanic mechanic;
	private ActingPlayer actingPlayer;
	private Player<?> squig;
	private Player<?> other;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		game = mock(Game.class);
		mechanic = mock(GameMechanic.class);
		FieldModel fieldModel = mock(FieldModel.class);
		squig = mock(Player.class);
		other = mock(Player.class);

		when(squig.getId()).thenReturn("squig");
		when(other.getId()).thenReturn("other");
		when(game.getPlayerById("squig")).thenAnswer(invocation -> squig);
		when(game.getPlayerById("other")).thenAnswer(invocation -> other);
		when(game.getPlayers()).thenReturn(new Player<?>[]{squig, other});
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getTurnMode()).thenReturn(TurnMode.REGULAR);
		doReturn(mechanic).when(game).getMechanic(Mechanic.Type.GAME);
		when(mechanic.isActivationWithoutDeclaredAction(PlayerAction.SQUIG_RAGE)).thenReturn(true);

		when(fieldModel.getPlayerState(any())).thenAnswer(invocation -> states.get(invocation.<Player<?>>getArgument(0)));
		doAnswer(invocation -> states.put(invocation.getArgument(0), invocation.getArgument(1)))
			.when(fieldModel).setPlayerState(any(), any());

		states.put(squig, STANDING);
		states.put(other, STANDING);

		actingPlayer = new ActingPlayer(game);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
	}

	@Test
	void activationWithoutDeclaredActionKeepsBase() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);

		assertEquals(PlayerState.STANDING, states.get(squig).getBase());
		assertEquals(PlayerAction.SQUIG_RAGE, actingPlayer.getPlayerAction());
	}

	@Test
	void regularActivationShowsPlayerAsMoving() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.MOVE, false);

		assertEquals(PlayerState.MOVING, states.get(squig).getBase());
	}

	@Test
	void declaringActionAfterActivationShowsPlayerAsMoving() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);
		actingPlayer.markSkillUsed(squigRage);

		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.MOVE, false);

		assertEquals(PlayerState.MOVING, states.get(squig).getBase());
		assertEquals(PlayerAction.MOVE, actingPlayer.getPlayerAction());
		assertTrue(actingPlayer.isSkillUsed(squigRage));
	}

	@Test
	void deselectingAfterRollWithoutDeclaringActionDeactivates() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);
		actingPlayer.markSkillUsed(squigRage);

		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		assertNull(actingPlayer.getPlayerId());
		assertEquals(PlayerState.STANDING, states.get(squig).getBase());
		assertFalse(states.get(squig).isActive());
	}

	@Test
	void deselectingBeforeRollKeepsPlayerActive() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);

		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		assertTrue(states.get(squig).isActive());
	}

	@Test
	void switchingToAnotherPlayerAfterRollDeactivates() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);
		actingPlayer.markSkillUsed(squigRage);

		UtilActingPlayer.changeActingPlayer(game, "other", PlayerAction.MOVE, false);

		assertFalse(states.get(squig).isActive());
		assertEquals(PlayerState.MOVING, states.get(other).getBase());
	}

	@Test
	void pronePlayerDeselectedAfterDeclaringActionRemainsProneWhenMechanicSaysSo() {
		states.put(squig, PRONE);
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.SQUIG_RAGE, false);
		actingPlayer.markSkillUsed(squigRage);
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.MOVE, false);
		when(mechanic.remainsProneWhenDeactivated(actingPlayer)).thenReturn(true);

		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		assertEquals(PlayerState.PRONE, states.get(squig).getBase());
		assertFalse(states.get(squig).isActive());
	}

	@Test
	void playerWhoActedIsStandingAndInactiveAfterDeselecting() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.MOVE, false);
		actingPlayer.setHasMoved(true);

		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		assertEquals(PlayerState.STANDING, states.get(squig).getBase());
		assertFalse(states.get(squig).isActive());
	}

	@Test
	void playerWhoDidNotActStaysActiveAfterDeselecting() {
		UtilActingPlayer.changeActingPlayer(game, "squig", PlayerAction.MOVE, false);

		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		assertEquals(PlayerState.STANDING, states.get(squig).getBase());
		assertTrue(states.get(squig).isActive());
	}
}

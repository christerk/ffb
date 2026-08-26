package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.server.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@MockitoSettings(strictness = Strictness.LENIENT)
class UtilServerPlayerMoveJumpArrayTest {

	private static final String PLAYER_ID = "jump-player";
	private static final FieldCoordinate FROM = new FieldCoordinate(8, 7);
	private static final FieldCoordinate INTERMEDIATE = new FieldCoordinate(9, 7);
	private static final FieldCoordinate FINAL = new FieldCoordinate(10, 7);

	@Mock
	private GameState gameState;
	@Mock
	private Game game;
	@Mock
	private ActingPlayer actingPlayer;
	@Mock
	private Player player;
	@Mock
	private FieldModel fieldModel;
	@Mock
	private MechanicsFactory mechanicsFactory;
	@Mock
	private JumpMechanic jumpMechanic;
	@Mock
	private MoveSquare offeredMove;
	@Mock
	private TurnData turnData;

	@BeforeEach
	void setUp() {
		given(gameState.getGame()).willReturn(game);
		given(game.getActingPlayer()).willReturn(actingPlayer);
		given(actingPlayer.getPlayer()).willReturn(player);
		given(actingPlayer.getPlayerId()).willReturn(PLAYER_ID);
		given(game.getFieldModel()).willReturn(fieldModel);
		given(fieldModel.getPlayerCoordinate(player)).willReturn(FROM);
		given(fieldModel.getMoveSquare(FINAL)).willReturn(offeredMove);
		given(game.getFactory(FactoryType.Factory.MECHANIC)).willReturn(mechanicsFactory);
		given(game.getTurnData()).willReturn(turnData);
		given(mechanicsFactory.forName(anyString())).willReturn(jumpMechanic);
		given(jumpMechanic.isValidJump(game, player, FROM, FINAL)).willReturn(true);
		given(actingPlayer.isJumping()).willReturn(true);
	}

	@Test
	void normalizesLegacyJumpArrayToItsOfferedDestination() {
		ClientCommandMove command = move(FROM, INTERMEDIATE, FINAL);

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, command, true);

		assertTrue(result.isAccepted());
		assertTrue(result.isNormalized());
		assertEquals(FROM, result.getCoordinateFrom());
		assertArrayEquals(new FieldCoordinate[] { FINAL }, result.getMoveStack());
	}

	@Test
	void leavesCurrentSingletonJumpCommandUnchanged() {
		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, move(FROM, FINAL), true);

		assertTrue(result.isAccepted());
		assertFalse(result.isNormalized());
		assertArrayEquals(new FieldCoordinate[] { FINAL }, result.getMoveStack());
	}

	@Test
	void leavesOrdinaryMovementArrayUnchanged() {
		given(actingPlayer.isJumping()).willReturn(false);

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, move(FROM, INTERMEDIATE, FINAL), true);

		assertTrue(result.isAccepted());
		assertFalse(result.isNormalized());
		assertArrayEquals(new FieldCoordinate[] { INTERMEDIATE, FINAL }, result.getMoveStack());
	}

	@Test
	void rejectsStaleOfferButAllowsRetryWhenTheSameOfferReturns() {
		ClientCommandMove command = move(FROM, INTERMEDIATE, FINAL);
		given(fieldModel.getMoveSquare(FINAL)).willReturn(null, offeredMove);

		UtilServerPlayerMove.MoveStackValidation rejected =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, command, true);
		UtilServerPlayerMove.MoveStackValidation retried =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, command, true);

		assertFalse(rejected.isAccepted());
		assertArrayEquals(new FieldCoordinate[0], rejected.getMoveStack());
		assertTrue(retried.isAccepted());
		assertArrayEquals(new FieldCoordinate[] { FINAL }, retried.getMoveStack());
	}

	@Test
	void rejectsDestinationThatTheCurrentRulesetNoLongerAllows() {
		given(jumpMechanic.isValidJump(game, player, FROM, FINAL)).willReturn(false);

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, move(FROM, INTERMEDIATE, FINAL), true);

		assertFalse(result.isAccepted());
		assertArrayEquals(new FieldCoordinate[0], result.getMoveStack());
	}

	@Test
	void transformsAwayCoordinatesExactlyOnceBeforeValidationAndNormalization() {
		FieldCoordinate clientFrom = FROM.transform();
		FieldCoordinate clientIntermediate = INTERMEDIATE.transform();
		FieldCoordinate clientFinal = FINAL.transform();

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState,
				move(clientFrom, clientIntermediate, clientFinal), false);

		assertTrue(result.isAccepted());
		assertEquals(FROM, result.getCoordinateFrom());
		assertArrayEquals(new FieldCoordinate[] { FINAL }, result.getMoveStack());
	}

	@Test
	void appliesTheSameNormalizationContractToBlitzMove() {
		ClientCommandBlitzMove command = new ClientCommandBlitzMove(PLAYER_ID, FROM,
			new FieldCoordinate[] { INTERMEDIATE, FINAL });

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, command, true);

		assertTrue(result.isAccepted());
		assertTrue(result.isNormalized());
		assertArrayEquals(new FieldCoordinate[] { FINAL }, result.getMoveStack());
	}

	@Test
	void rejectedBlitzLeavesTeamActionAndTargetSelectionStateUntouched() {
		given(fieldModel.getMoveSquare(FINAL)).willReturn(null);
		ClientCommandBlitzMove command = new ClientCommandBlitzMove(PLAYER_ID, FROM,
			new FieldCoordinate[] { INTERMEDIATE, FINAL });

		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, command, true);

		assertFalse(result.isAccepted());
		verifyNoInteractions(turnData);
		verify(fieldModel, never()).setTargetSelectionState(any(TargetSelectionState.class));
	}

	@Test
	void retainedStackRejectsNewMoveAndBlitzWithoutPublishingAnotherStack() {
		FieldCoordinate[] retained = new FieldCoordinate[] { INTERMEDIATE };

		UtilServerPlayerMove.MoveStackValidation moveResult =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, move(FROM, FINAL), true, retained);
		UtilServerPlayerMove.MoveStackValidation blitzResult =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState,
				new ClientCommandBlitzMove(PLAYER_ID, FROM, new FieldCoordinate[] { FINAL }), true, retained);

		assertFalse(moveResult.isAccepted());
		assertFalse(blitzResult.isAccepted());
		assertArrayEquals(new FieldCoordinate[0], moveResult.getMoveStack());
		assertArrayEquals(new FieldCoordinate[0], blitzResult.getMoveStack());
	}

	@Test
	void acceptedResultDoesNotExposeItsInternalStack() {
		UtilServerPlayerMove.MoveStackValidation result =
			UtilServerPlayerMove.validateAndFetchMoveStack(gameState, move(FROM, FINAL), true);
		FieldCoordinate[] firstRead = result.getMoveStack();
		firstRead[0] = mock(FieldCoordinate.class);

		assertArrayEquals(new FieldCoordinate[] { FINAL }, result.getMoveStack());
	}

	private ClientCommandMove move(FieldCoordinate from, FieldCoordinate... to) {
		return new ClientCommandMove(PLAYER_ID, from, to, null);
	}
}

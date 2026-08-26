package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.factory.common.GoForItModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.OnTheBallMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.GoForItContext;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.net.commands.ClientCommandBlitzMove;
import com.fumbbl.ffb.net.commands.ClientCommandMove;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPassing;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class UtilServerPlayerMove {

	public static MoveStackValidation validateAndFetchMoveStack(GameState pGameState,
		ClientCommandMove pMoveCommand, boolean pHomeCommand) {
		return validateAndFetchMoveStack(pGameState, pMoveCommand, pHomeCommand, null);
	}

	public static MoveStackValidation validateAndFetchMoveStack(GameState pGameState,
		ClientCommandMove pMoveCommand, boolean pHomeCommand, FieldCoordinate[] pRetainedMoveStack) {
		if (ArrayTool.isProvided(pRetainedMoveStack)) {
			return MoveStackValidation.rejected();
		}
		if (pMoveCommand == null) {
			return MoveStackValidation.rejected();
		}
		return validateAndFetchMoveStack(pGameState, pMoveCommand.getCoordinateFrom(),
			pMoveCommand.getCoordinatesTo(), pHomeCommand, pMoveCommand.getId().name());
	}

	public static MoveStackValidation validateAndFetchMoveStack(GameState pGameState,
		ClientCommandBlitzMove pMoveCommand, boolean pHomeCommand) {
		return validateAndFetchMoveStack(pGameState, pMoveCommand, pHomeCommand, null);
	}

	public static MoveStackValidation validateAndFetchMoveStack(GameState pGameState,
		ClientCommandBlitzMove pMoveCommand, boolean pHomeCommand, FieldCoordinate[] pRetainedMoveStack) {
		if (ArrayTool.isProvided(pRetainedMoveStack)) {
			return MoveStackValidation.rejected();
		}
		if (pMoveCommand == null) {
			return MoveStackValidation.rejected();
		}
		return validateAndFetchMoveStack(pGameState, pMoveCommand.getCoordinateFrom(),
			pMoveCommand.getCoordinatesTo(), pHomeCommand, pMoveCommand.getId().name());
	}

	private static MoveStackValidation validateAndFetchMoveStack(GameState pGameState,
		FieldCoordinate pCoordinateFrom, FieldCoordinate[] pCoordinatesTo, boolean pHomeCommand,
		String pCommandName) {
		if ((pGameState == null) || (pCoordinateFrom == null) || !ArrayTool.isProvided(pCoordinatesTo)) {
			return MoveStackValidation.rejected();
		}

		Game game = pGameState.getGame();
		if ((game == null) || (game.getActingPlayer() == null) || (game.getActingPlayer().getPlayer() == null)) {
			return MoveStackValidation.rejected();
		}

		FieldCoordinate coordinateFrom = pHomeCommand ? pCoordinateFrom : pCoordinateFrom.transform();
		FieldCoordinate[] moveStack = fetchMoveStack(pCoordinatesTo, pHomeCommand);
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if ((playerCoordinate == null) || !playerCoordinate.equals(coordinateFrom)) {
			logMoveCommand(pGameState, pHomeCommand, IServerLogLevel.DEBUG,
				"!Client move out of sync, Command dropped");
			return MoveStackValidation.rejected();
		}

		if (!actingPlayer.isJumping()) {
			return MoveStackValidation.accepted(coordinateFrom, moveStack, false);
		}

		FieldCoordinate finalDestination = moveStack[moveStack.length - 1];
		if ((finalDestination == null) || !FieldCoordinateBounds.FIELD.isInBounds(finalDestination)) {
			logJumpRejection(pGameState, pHomeCommand, pCommandName, "destination missing or out of bounds");
			return MoveStackValidation.rejected();
		}

		MoveSquare offeredMove = game.getFieldModel().getMoveSquare(finalDestination);
		if (offeredMove == null) {
			logJumpRejection(pGameState, pHomeCommand, pCommandName, "destination not currently offered");
			return MoveStackValidation.rejected();
		}

		JumpMechanic jumpMechanic = (JumpMechanic) game.getFactory(Factory.MECHANIC)
			.forName(Mechanic.Type.JUMP.name());
		if ((jumpMechanic == null) || !jumpMechanic.isValidJump(game, actingPlayer.getPlayer(),
			coordinateFrom, finalDestination)) {
			logJumpRejection(pGameState, pHomeCommand, pCommandName, "destination invalid for ruleset");
			return MoveStackValidation.rejected();
		}

		if (moveStack.length == 1) {
			return MoveStackValidation.accepted(coordinateFrom, moveStack, false);
		}

		logMoveCommand(pGameState, pHomeCommand, IServerLogLevel.WARN,
			"!Legacy jump path normalized command=" + pCommandName
				+ " player=" + actingPlayer.getPlayerId()
				+ " count=" + moveStack.length
				+ " from=" + coordinateFrom
				+ " final=" + finalDestination);
		return MoveStackValidation.accepted(coordinateFrom,
			new FieldCoordinate[] { finalDestination }, true);
	}

	private static void logJumpRejection(GameState pGameState, boolean pHomeCommand,
		String pCommandName, String pReason) {
		logMoveCommand(pGameState, pHomeCommand, IServerLogLevel.DEBUG,
			"!Jump move dropped command=" + pCommandName + " reason=" + pReason);
	}

	private static void logMoveCommand(GameState pGameState, boolean pHomeCommand, int pLevel,
		String pMessage) {
		if ((pGameState == null) || (pGameState.getGame() == null) || (pGameState.getServer() == null)
			|| (pGameState.getServer().getDebugLog() == null)) {
			return;
		}
		pGameState.getServer().getDebugLog().log(pLevel, pGameState.getGame().getId(),
			pHomeCommand ? DebugLog.COMMAND_CLIENT_HOME : DebugLog.COMMAND_CLIENT_AWAY, pMessage);
	}

	/**
	 * Returns whether the acting player must dodge when leaving its current square.
	 * This is the authoritative rule used both when publishing move offers and when
	 * consuming each coordinate of a retained movement stack.
	 */
	public static boolean isDodgeRequired(Game pGame, ActingPlayer pActingPlayer) {
		return (pGame != null) && (pActingPlayer != null) && (pActingPlayer.getPlayer() != null)
			&& !pActingPlayer.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenMoving)
			&& (UtilPlayer.findTacklezones(pGame, pActingPlayer.getPlayer()) > 0);
	}

	/**
	 * Derives the dodge flag from the live departure square and records any
	 * disagreement with the client-facing offer metadata. A jump owns its own
	 * escape roll and therefore never also dodges.
	 */
	public static boolean deriveDodgeRequired(GameState pGameState, FieldCoordinate pCoordinateTo,
		MoveSquare pOfferedMove) {
		if ((pGameState == null) || (pGameState.getGame() == null)) {
			return false;
		}
		Game game = pGameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		boolean jumping = (actingPlayer != null) && actingPlayer.isJumping();
		boolean derived = (actingPlayer != null) && !jumping
			&& isDodgeRequired(game, actingPlayer);
		Boolean offered = (pOfferedMove == null) ? null : pOfferedMove.isDodging();
		// Jump offers store their jump target in MoveSquare.minimumRollDodge, so
		// MoveSquare.isDodging() is intentionally not a dodge signal on this path.
		if (!jumping && ((derived && (offered == null)) || ((offered != null) && (offered != derived)))) {
			logDodgeDerivationDivergence(pGameState, actingPlayer, pCoordinateTo, offered, derived);
		}
		return derived;
	}

	private static void logDodgeDerivationDivergence(GameState pGameState, ActingPlayer pActingPlayer,
		FieldCoordinate pCoordinateTo, Boolean pOffered, boolean pDerived) {
		if ((pGameState.getServer() == null) || (pGameState.getServer().getDebugLog() == null)) {
			return;
		}
		String commandKind = (pActingPlayer == null) || (pActingPlayer.getPlayerAction() == null)
			? "UNKNOWN" : pActingPlayer.getPlayerAction().name();
		String playerId = (pActingPlayer == null) ? null : pActingPlayer.getPlayerId();
		pGameState.getServer().getDebugLog().log(IServerLogLevel.WARN, pGameState.getGame().getId(),
			"!Dodge derivation divergence command=" + commandKind
				+ " player=" + playerId
				+ " square=" + pCoordinateTo
				+ " offered=" + pOffered
				+ " derived=" + pDerived);
	}

	public static final class MoveStackValidation {
		private final boolean accepted;
		private final FieldCoordinate coordinateFrom;
		private final FieldCoordinate[] moveStack;
		private final boolean normalized;

		private MoveStackValidation(boolean pAccepted, FieldCoordinate pCoordinateFrom,
			FieldCoordinate[] pMoveStack, boolean pNormalized) {
			accepted = pAccepted;
			coordinateFrom = pCoordinateFrom;
			moveStack = copyOf(pMoveStack);
			normalized = pNormalized;
		}

		private static MoveStackValidation accepted(FieldCoordinate pCoordinateFrom,
			FieldCoordinate[] pMoveStack, boolean pNormalized) {
			return new MoveStackValidation(true, pCoordinateFrom, pMoveStack, pNormalized);
		}

		private static MoveStackValidation rejected() {
			return new MoveStackValidation(false, null, new FieldCoordinate[0], false);
		}

		public boolean isAccepted() {
			return accepted;
		}

		public FieldCoordinate getCoordinateFrom() {
			return coordinateFrom;
		}

		public FieldCoordinate[] getMoveStack() {
			return copyOf(moveStack);
		}

		public boolean isNormalized() {
			return normalized;
		}

		private static FieldCoordinate[] copyOf(FieldCoordinate[] pCoordinates) {
			if (pCoordinates == null) {
				return new FieldCoordinate[0];
			}
			FieldCoordinate[] result = new FieldCoordinate[pCoordinates.length];
			System.arraycopy(pCoordinates, 0, result, 0, pCoordinates.length);
			return result;
		}
	}

	public static void updateMoveSquares(GameState pGameState, boolean jumping) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() != null) {
			fieldModel.clearMoveSquares();
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
			if (actingPlayer.getPlayerAction().isMoving() && UtilPlayer.isNextMovePossible(game, jumping)
					&& FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
				if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.movesRandomly)) {
					for (int x = -1; x < 2; x += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(x, 0);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addMoveSquare(pGameState, jumping, moveCoordinate);
						}
					}
					for (int y = -1; y < 2; y += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(0, y);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addMoveSquare(pGameState, jumping, moveCoordinate);
						}
					}
				} else {
					int steps = jumping ? 2 : 1;
					Set<FieldCoordinate> validPassBlockCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
					FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(playerCoordinate,
							FieldCoordinateBounds.FIELD, steps, false);
					JumpMechanic mechanic = (JumpMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
					boolean canStillJump = mechanic.canStillJump(game, actingPlayer);
					OnTheBallMechanic onTheBallMechanic = (OnTheBallMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
					for (FieldCoordinate coordinate : adjacentCoordinates) {
						if (fieldModel.getPlayer(coordinate) == null) {
							if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
								int distance = coordinate.distanceInSteps(playerCoordinate);
								if (onTheBallMechanic.validPassBlockMove(game, actingPlayer, playerCoordinate, coordinate,
									validPassBlockCoordinates, canStillJump, distance)) {
									addMoveSquare(pGameState, jumping, coordinate);
								}
							} else if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
								FieldCoordinateBounds bounds = game.isHomePlaying() ? FieldCoordinateBounds.HALF_HOME
										: FieldCoordinateBounds.HALF_AWAY;
								if (bounds.isInBounds(coordinate)) {
									addMoveSquare(pGameState, jumping, coordinate);
								}
							} else {
								addMoveSquare(pGameState, jumping, coordinate);
							}
						}
					}
				}
			}
		}
	}

	private static void addMoveSquare(GameState pGameState, boolean jumping, FieldCoordinate pCoordinate) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());

		JumpMechanic jumpMechanic = (JumpMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		if (jumping && !jumpMechanic.isValidJump(game, actingPlayer.getPlayer(), playerCoordinate, pCoordinate)) {
			return;
		}

		boolean goForIt;
		int minimumRollDodge = 0;
		boolean dodging = isDodgeRequired(game, actingPlayer);
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		if (jumping) {
			JumpModifierFactory modifierFactory = game.getFactory(FactoryType.Factory.JUMP_MODIFIER);
			Set<JumpModifier> jumpModifiers = new HashSet<>();
			if (!actingPlayer.isJumpsWithoutModifiers()) {
				jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer(), playerCoordinate, pCoordinate));
			}
			minimumRollDodge = mechanic.minimumRollJump(actingPlayer.getPlayer(), jumpModifiers);
			if (actingPlayer.isStandingUp() && !actingPlayer.hasActed()
					&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
				goForIt = ((3 + playerCoordinate.distanceInSteps(pCoordinate)) > actingPlayer.getPlayer().getMovementWithModifiers());
			} else {
				goForIt = ((actingPlayer.getCurrentMove() + playerCoordinate.distanceInSteps(pCoordinate)) > actingPlayer.getPlayer().getMovementWithModifiers());
			}
		} else {
			goForIt = UtilPlayer.isNextMoveGoingForIt(game);
			if (dodging) {
				DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);
				Set<DodgeModifier> dodgeModifiers = modifierFactory.findModifiers(new DodgeContext(game, actingPlayer, playerCoordinate, pCoordinate));
				minimumRollDodge = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(),
						dodgeModifiers);
			}
		}
		int minimumRollGoForIt = 0;
		if (goForIt) {
			GoForItModifierFactory factory = game.getFactory(Factory.GO_FOR_IT_MODIFIER);
			Set<GoForItModifier> goForItModifiers = factory.findModifiers(new GoForItContext(game, actingPlayer.getPlayer(), pGameState.getPrayerState().getMolesUnderThePitch()));
			minimumRollGoForIt = DiceInterpreter.getInstance().minimumRollGoingForIt(goForItModifiers);
		}
		MoveSquare moveSquare = new MoveSquare(pCoordinate, minimumRollDodge, minimumRollGoForIt);
		fieldModel.add(moveSquare);
	}

	public static FieldCoordinate[] fetchMoveStack(ClientCommandMove pMoveCommand,
	                                               boolean pHomeCommand) {
		if ((pMoveCommand == null) || !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return new FieldCoordinate[0];
		}
		FieldCoordinate[] coordinatesTo = pMoveCommand.getCoordinatesTo();
		return fetchMoveStack(coordinatesTo, pHomeCommand);
	}

	public static FieldCoordinate[] fetchMoveStack(ClientCommandBlitzMove pMoveCommand,
	                                               boolean pHomeCommand) {
		if ((pMoveCommand == null) || !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return new FieldCoordinate[0];
		}
		FieldCoordinate[] coordinatesTo = pMoveCommand.getCoordinatesTo();
		return fetchMoveStack(coordinatesTo, pHomeCommand);
	}

	private static FieldCoordinate[] fetchMoveStack(FieldCoordinate[] coordinatesTo, boolean pHomeCommand) {
		FieldCoordinate[] moveStack = new FieldCoordinate[coordinatesTo.length];
		if (pHomeCommand) {
			System.arraycopy(coordinatesTo, 0, moveStack, 0, moveStack.length);
		} else {
			for (int i = 0; i < moveStack.length; i++) {
				moveStack[i] = coordinatesTo[i].transform();
			}
		}
		return moveStack;
	}

	public static FieldCoordinate fetchFromSquare(ClientCommandMove moveCommand,
	                                              boolean homeCommand)  {
		return fetchFromSquare(moveCommand.getCoordinateFrom(), homeCommand);
	}

	public static FieldCoordinate fetchFromSquare(ClientCommandBlitzMove moveCommand,
	                                              boolean homeCommand)  {
		return fetchFromSquare(moveCommand.getCoordinateFrom(), homeCommand);
	}

	private static FieldCoordinate fetchFromSquare(FieldCoordinate from, boolean homeCommand) {
		return homeCommand ? from : from.transform();
	}
}

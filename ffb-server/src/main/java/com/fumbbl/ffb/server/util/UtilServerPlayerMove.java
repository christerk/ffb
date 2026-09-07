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
import com.fumbbl.ffb.model.Player;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class UtilServerPlayerMove {

	public static boolean isValidMove(GameState pGameState, ClientCommandMove pMoveCommand, boolean pHomeCommand) {
		if ((pMoveCommand == null) || (pMoveCommand.getCoordinateFrom() == null)
				|| !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return false;
		}
		FieldCoordinate coordinateFrom = pHomeCommand ? pMoveCommand.getCoordinateFrom()
				: pMoveCommand.getCoordinateFrom().transform();
		return isValidMove(pGameState, coordinateFrom, pHomeCommand);
	}

	public static boolean isValidMove(GameState pGameState, ClientCommandBlitzMove pMoveCommand, boolean pHomeCommand) {
		if ((pMoveCommand == null) || (pMoveCommand.getCoordinateFrom() == null)
				|| !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return false;
		}
		FieldCoordinate coordinateFrom = pHomeCommand ? pMoveCommand.getCoordinateFrom()
				: pMoveCommand.getCoordinateFrom().transform();
		return isValidMove(pGameState, coordinateFrom, pHomeCommand);
	}

	private static boolean isValidMove(GameState pGameState, FieldCoordinate coordinateFrom, boolean pHomeCommand) {
		Game game = pGameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if ((playerCoordinate != null) && playerCoordinate.equals(coordinateFrom)) {
			return true;
		}
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, game.getId(),
			pHomeCommand ? DebugLog.COMMAND_CLIENT_HOME : DebugLog.COMMAND_CLIENT_AWAY,
			"!Client move out of sync, Command dropped");
		return false;
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
		boolean dodging = !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenMoving)
				&& (UtilPlayer.findTacklezones(game, actingPlayer.getPlayer()) > 0);
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

	/**
	 * Trims a client-supplied move path down to the leading squares that are
	 * guaranteed to require no roll (dodge, rush/going-for-it or jump).
	 * <p>
	 * The client is trusted to only ever request a multi-square move when the
	 * whole path is free of rolls, but a modified or malicious client could send
	 * a path that skips over squares that would actually require a dodge, GFI or
	 * jump roll. Since the server only ever recomputes the reachable squares
	 * (MoveSquare cache) for the player's actual position - which matches only
	 * the first element of the path - every following element has to be
	 * re-validated here against the field state as it would be after the
	 * preceding steps of the path were taken.
	 * <p>
	 * The first element of the path is not trimmed for requiring a dodge or
	 * rush/going-for-it roll, since it is validated against the actual,
	 * up-to-date MoveSquare cache elsewhere and may legitimately require such a
	 * roll, which is then handled by the normal move sequence. However, if the
	 * first element is not adjacent to the starting square, it can only be
	 * reached by a jump, so it is rejected outright (resulting in an empty move
	 * path) unless jumping was flagged and the target is actually a valid jump
	 * destination (correct distance/direction and jump allowed); StepMove would
	 * otherwise move the player there unconditionally without ever validating
	 * the jump. Every following element that would require a roll is dropped,
	 * together with all elements after it, so that the sequence stops there and
	 * waits for a new command from the client.
	 */
	public static FieldCoordinate[] trimUnsafeMoveStack(GameState pGameState, FieldCoordinate pCoordinateFrom,
	                                                     FieldCoordinate[] pMoveStack, boolean pJumping) {
		if (!ArrayTool.isProvided(pMoveStack) || (pCoordinateFrom == null)) {
			return pMoveStack;
		}
		Game game = pGameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == null) {
			return pMoveStack;
		}
		FieldCoordinate firstCoordinate = pMoveStack[0];
		if (pJumping) {
			JumpMechanic jumpMechanic = (JumpMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
			if (!jumpMechanic.isValidJump(game, actingPlayer.getPlayer(), pCoordinateFrom, firstCoordinate)) {
				return new FieldCoordinate[0];
			}
		} else if (pCoordinateFrom.distanceInSteps(firstCoordinate) != 1) {
			// not adjacent and not a flagged jump: can't be reached in a single, roll-free step
			return new FieldCoordinate[0];
		}
		FieldCoordinate previousCoordinate = firstCoordinate;
		int simulatedCurrentMove = actingPlayer.getCurrentMove() + (pJumping ? 2 : 1);
		for (int i = 1; i < pMoveStack.length; i++) {
			FieldCoordinate coordinate = pMoveStack[i];
			if (stepRequiresRoll(game, actingPlayer, previousCoordinate, coordinate, simulatedCurrentMove)) {
				return Arrays.copyOf(pMoveStack, i);
			}
			simulatedCurrentMove++;
			previousCoordinate = coordinate;
		}
		return pMoveStack;
	}

	private static boolean stepRequiresRoll(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to,
	                                         int simulatedCurrentMove) {
		if (from.distanceInSteps(to) != 1) {
			// anything that is not a simple adjacent step (e.g. an unflagged jump)
			// must be confirmed by the client one square at a time
			return true;
		}
		Player<?> player = actingPlayer.getPlayer();
		boolean dodging = !player.hasSkillProperty(NamedProperties.ignoreTacklezonesWhenMoving)
				&& (UtilPlayer.findTacklezones(game, player, from) > 0);
		return dodging || simulatedGoForIt(game, actingPlayer, simulatedCurrentMove);
	}

	private static boolean simulatedGoForIt(Game game, ActingPlayer actingPlayer, int simulatedCurrentMove) {
		Player<?> player = actingPlayer.getPlayer();
		if ((game.getTurnMode() == TurnMode.KICKOFF_RETURN) || (game.getTurnMode() == TurnMode.PASS_BLOCK)) {
			return false;
		}
		if (actingPlayer.isStandingUp() && !actingPlayer.hasActed()
				&& !player.hasSkillProperty(NamedProperties.canStandUpForFree)) {
			return (3 >= player.getMovementWithModifiers());
		}
		return simulatedCurrentMove >= player.getMovementWithModifiers();
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

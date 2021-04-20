package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.factory.GoForItModifierFactory;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
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
		pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG,
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
					for (FieldCoordinate coordinate : adjacentCoordinates) {
						if (fieldModel.getPlayer(coordinate) == null) {
							if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
								int distance = coordinate.distanceInSteps(playerCoordinate);
								if (validPassBlockCoordinates.contains(coordinate)
										|| ArrayTool.isProvided(PathFinderWithPassBlockSupport.allowPassBlockMove(game,
												actingPlayer.getPlayer(), coordinate, 3 - distance - actingPlayer.getCurrentMove(),
												canStillJump))) {
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
			Set<JumpModifier> jumpModifiers = modifierFactory.findModifiers(new JumpContext(game, actingPlayer.getPlayer(), playerCoordinate, pCoordinate));
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
			Set<GoForItModifier> goForItModifiers = factory.findModifiers(new GoForItContext(game, actingPlayer.getPlayer()));
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

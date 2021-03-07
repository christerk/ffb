package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PathFinderWithPassBlockSupport;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.factory.DodgeModifierFactory;
import com.balancedbytes.games.ffb.factory.GoForItModifierFactory;
import com.balancedbytes.games.ffb.factory.LeapModifierFactory;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.DodgeContext;
import com.balancedbytes.games.ffb.modifiers.DodgeModifier;
import com.balancedbytes.games.ffb.modifiers.GoForItContext;
import com.balancedbytes.games.ffb.modifiers.GoForItModifier;
import com.balancedbytes.games.ffb.modifiers.LeapContext;
import com.balancedbytes.games.ffb.modifiers.LeapModifier;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilPlayer;

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

	public static void updateMoveSquares(GameState pGameState, boolean pLeaping) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() != null) {
			fieldModel.clearMoveSquares();
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
			if (actingPlayer.getPlayerAction().isMoving() && UtilPlayer.isNextMovePossible(game, pLeaping)
					&& FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
				if (actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.movesRandomly)) {
					for (int x = -1; x < 2; x += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(x, 0);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addMoveSquare(pGameState, pLeaping, moveCoordinate);
						}
					}
					for (int y = -1; y < 2; y += 2) {
						FieldCoordinate moveCoordinate = playerCoordinate.add(0, y);
						if (FieldCoordinateBounds.FIELD.isInBounds(moveCoordinate)) {
							addMoveSquare(pGameState, pLeaping, moveCoordinate);
						}
					}
				} else {
					int steps = pLeaping ? 2 : 1;
					Set<FieldCoordinate> validPassBlockCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
					FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(playerCoordinate,
							FieldCoordinateBounds.FIELD, steps, false);
					for (FieldCoordinate coordinate : adjacentCoordinates) {
						if (fieldModel.getPlayer(coordinate) == null) {
							if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
								int distance = coordinate.distanceInSteps(playerCoordinate);
								if (validPassBlockCoordinates.contains(coordinate)
										|| ArrayTool.isProvided(PathFinderWithPassBlockSupport.allowPassBlockMove(game,
												actingPlayer.getPlayer(), coordinate, 3 - distance - actingPlayer.getCurrentMove(),
												UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canLeap)))) {
									addMoveSquare(pGameState, pLeaping, coordinate);
								}
							} else if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
								FieldCoordinateBounds bounds = game.isHomePlaying() ? FieldCoordinateBounds.HALF_HOME
										: FieldCoordinateBounds.HALF_AWAY;
								if (bounds.isInBounds(coordinate)) {
									addMoveSquare(pGameState, pLeaping, coordinate);
								}
							} else {
								addMoveSquare(pGameState, pLeaping, coordinate);
							}
						}
					}
				}
			}
		}
	}

	private static void addMoveSquare(GameState pGameState, boolean pLeaping, FieldCoordinate pCoordinate) {
		Game game = pGameState.getGame();
		FieldModel fieldModel = game.getFieldModel();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
		boolean goForIt;
		int minimumRollDodge = 0;
		boolean dodging = !actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.ignoreTacklezonesWhenMoving)
				&& (UtilPlayer.findTacklezones(game, actingPlayer.getPlayer()) > 0);
		AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		if (pLeaping) {
			LeapModifierFactory modifierFactory = new LeapModifierFactory();
			Set<LeapModifier> leapModifiers = modifierFactory.findModifiers(new LeapContext(game, actingPlayer.getPlayer()));
			minimumRollDodge = mechanic.minimumRollLeap(actingPlayer.getPlayer(), leapModifiers);
			if (actingPlayer.isStandingUp() && !actingPlayer.hasActed()
					&& !actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canStandUpForFree)) {
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

	public static FieldCoordinate[] fetchMoveStack(GameState pGameState, ClientCommandMove pMoveCommand,
			boolean pHomeCommand) {
		if ((pGameState == null) || (pMoveCommand == null) || !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return new FieldCoordinate[0];
		}
		FieldCoordinate[] coordinatesTo = pMoveCommand.getCoordinatesTo();
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

}

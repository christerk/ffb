package com.balancedbytes.games.ffb.server.util;

import java.util.Set;

import com.balancedbytes.games.ffb.DodgeModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.GoForItModifier;
import com.balancedbytes.games.ffb.GoForItModifierFactory;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PathFinderWithPassBlockSupport;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandMove;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class UtilServerPlayerMove {
	
	public static boolean isValidMove(GameState pGameState, ClientCommandMove pMoveCommand, boolean pHomeCommand) {
		if ((pMoveCommand == null) || (pMoveCommand.getCoordinateFrom() == null) || !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return false;
		}
		FieldCoordinate coordinateFrom = pHomeCommand ? pMoveCommand.getCoordinateFrom() : pMoveCommand.getCoordinateFrom().transform();
    Game game = pGameState.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    if ((playerCoordinate != null) && playerCoordinate.equals(coordinateFrom)) {
    	return true;
    }
    pGameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, pHomeCommand ? DebugLog.COMMAND_CLIENT_HOME : DebugLog.COMMAND_CLIENT_AWAY, "!Client move out of sync, Command dropped");
    return false;
	}
	
  public static void updateMoveSquares(GameState pGameState, boolean pLeaping) {
    Game game = pGameState.getGame();
    FieldModel fieldModel = game.getFieldModel();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (actingPlayer.getPlayer() != null) {
      fieldModel.clearMoveSquares();
      FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
      if (actingPlayer.getPlayerAction().isMoving() && UtilPlayer.isNextMovePossible(game, pLeaping) && FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
      	if (UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)) {
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
	        FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(playerCoordinate, FieldCoordinateBounds.FIELD, steps, false);
	        for (FieldCoordinate coordinate : adjacentCoordinates) {
	          if (fieldModel.getPlayer(coordinate) == null) {
		        	if (game.getTurnMode() == TurnMode.PASS_BLOCK) {
		          	int distance = coordinate.distanceInSteps(playerCoordinate);
		          	if (validPassBlockCoordinates.contains(coordinate) || ArrayTool.isProvided(PathFinderWithPassBlockSupport.allowPassBlockMove(game, actingPlayer.getPlayer(), coordinate, 3 - distance - actingPlayer.getCurrentMove(), UtilCards.hasUnusedSkill(game, actingPlayer, Skill.LEAP)))) {
			          	addMoveSquare(pGameState, pLeaping, coordinate);
		          	}
		        	} else if (game.getTurnMode() == TurnMode.KICKOFF_RETURN) {
		        		FieldCoordinateBounds bounds = game.isHomePlaying() ? FieldCoordinateBounds.HALF_HOME : FieldCoordinateBounds.HALF_AWAY;
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
  	boolean goForIt = false;
    int minimumRollDodge = 0;
    boolean dodging = !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN) && (UtilPlayer.findTacklezones(game, actingPlayer.getPlayer()) > 0);
    if (pLeaping) {
      Set<LeapModifier> leapModifiers = LeapModifier.findLeapModifiers(game);
      minimumRollDodge = DiceInterpreter.getInstance().minimumRollLeap(actingPlayer.getPlayer(), leapModifiers);
      if (actingPlayer.isStandingUp() && !actingPlayer.hasActed() && !UtilCards.hasSkill(game, actingPlayer, Skill.JUMP_UP)) {
        goForIt = ((3 + playerCoordinate.distanceInSteps(pCoordinate)) > UtilCards.getPlayerMovement(game, actingPlayer.getPlayer()));
      } else {
        goForIt = ((actingPlayer.getCurrentMove() + playerCoordinate.distanceInSteps(pCoordinate)) > UtilCards.getPlayerMovement(game, actingPlayer.getPlayer()));
      }
    } else {
      goForIt = UtilPlayer.isNextMoveGoingForIt(game);
      if (dodging) {
        Set<DodgeModifier> dodgeModifiers = DodgeModifier.findDodgeModifiers(game, playerCoordinate, pCoordinate, 0);
        minimumRollDodge = DiceInterpreter.getInstance().minimumRollDodge(game, actingPlayer.getPlayer(), dodgeModifiers);
      }
    }
    int minimumRollGoForIt = 0;
    if (goForIt) {
      Set<GoForItModifier> goForItModifiers = new GoForItModifierFactory().findGoForItModifiers(game);
      minimumRollGoForIt = DiceInterpreter.getInstance().minimumRollGoingForIt(goForItModifiers);
    }
    MoveSquare moveSquare = new MoveSquare(pCoordinate, minimumRollDodge, minimumRollGoForIt);
    fieldModel.add(moveSquare);
  }

	public static FieldCoordinate[] fetchMoveStack(GameState pGameState, ClientCommandMove pMoveCommand, boolean pHomeCommand) {
		if ((pGameState == null) || (pMoveCommand == null) || !ArrayTool.isProvided(pMoveCommand.getCoordinatesTo())) {
			return new FieldCoordinate[0];
		}
	  FieldCoordinate[] coordinatesTo = pMoveCommand.getCoordinatesTo();
	  FieldCoordinate[] moveStack = new FieldCoordinate[coordinatesTo.length];
	  if (pHomeCommand) {
	  	for (int i = 0; i < moveStack.length; i++) {
	  		moveStack[i] = coordinatesTo[i];
	  	}
	  } else {
	  	for (int i = 0; i < moveStack.length; i++) {
	  		moveStack[i] = coordinatesTo[i].transform();
	  	}
	  }
	  return moveStack;
	}
  
}

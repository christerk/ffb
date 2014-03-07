package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ICommandWithActingPlayer;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerPlayerMove;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBlock;

/**
 * 
 * @author Kalimar
 */
public class UtilServerSteps {
  
  public static void validateStepId(IStep pStep, StepId pReceivedId) {
    if (pStep == null) {
      throw new IllegalArgumentException("Parameter step must not be null.");
    }
    if (pStep.getId() != pReceivedId) {
      throw new IllegalStateException("Wrong step id. Expected " + pStep.getId().getName() + " received " + ((pReceivedId != null) ? pReceivedId.getName() : "null"));
    }
  }
		
	public static boolean checkCommandIsFromCurrentPlayer(GameState pGameState, ReceivedCommand pReceivedCommand) {
    Game game = pGameState.getGame();
    if (game.isHomePlaying()) {
      return checkCommandIsFromHomePlayer(pGameState, pReceivedCommand);
    } else {
    	return checkCommandIsFromAwayPlayer(pGameState, pReceivedCommand);
    }
  }

	public static boolean checkCommandIsFromHomePlayer(GameState pGameState, ReceivedCommand pReceivedCommand) {
    return (pGameState.getServer().getSessionManager().getSessionOfHomeCoach(pGameState) == pReceivedCommand.getSession());
	}

	public static boolean checkCommandIsFromAwayPlayer(GameState pGameState, ReceivedCommand pReceivedCommand) {
    return (pGameState.getServer().getSessionManager().getSessionOfAwayCoach(pGameState) == pReceivedCommand.getSession());
	}
	
	public static boolean checkCommandWithActingPlayer(GameState pGameState, ICommandWithActingPlayer pActingPlayerCommand) {
    Game game = pGameState.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    return (StringTool.isProvided(pActingPlayerCommand.getActingPlayerId()) && pActingPlayerCommand.getActingPlayerId().equals(actingPlayer.getPlayerId()));
	}

  public static void changePlayerAction(IStep pStep, String pPlayerId, PlayerAction pPlayerAction, boolean pLeaping) {
    ActingPlayer actingPlayer = pStep.getGameState().getGame().getActingPlayer();
    UtilServerGame.changeActingPlayer(pStep, pPlayerId, pPlayerAction, pLeaping);
    if (StringTool.isProvided(pPlayerId)) {
    	UtilServerPlayerMove.updateMoveSquares(pStep.getGameState(), actingPlayer.isLeaping());
    	UtilBlock.updateDiceDecorations(pStep.getGameState().getGame());
    }
  }
  
  public static void sendAddedPlayers(GameState pGameState, Team pTeam, Player[] pAddedPlayers) {
    if (ArrayTool.isProvided(pAddedPlayers) && (pTeam != null)) {
      Game game = pGameState.getGame();
      FantasyFootballServer server = pGameState.getServer();
      for (Player addedPlayer : pAddedPlayers) {
        server.getCommunication().sendAddPlayer(pGameState, pTeam.getId(), addedPlayer, game.getFieldModel().getPlayerState(addedPlayer), game.getGameResult().getPlayerResult(addedPlayer));
      }
    }
  }
  
	public static boolean checkTouchdown(GameState pGameState) {
		boolean touchdown = false;
	  Game game = pGameState.getGame();
	  if (game.getFieldModel().isBallInPlay() && !game.getFieldModel().isBallMoving()) {
	    FieldCoordinate ballPosition = game.getFieldModel().getBallCoordinate();
	    Player ballCarrier = game.getFieldModel().getPlayer(ballPosition);
	    PlayerState ballCarrierState = game.getFieldModel().getPlayerState(ballCarrier);
	    ActingPlayer actingPlayer = game.getActingPlayer();
	    if ((ballCarrier != null) && (ballCarrierState != null) && ballCarrierState.hasTacklezones() && ((ballCarrier != actingPlayer.getPlayer()) || !actingPlayer.isSufferingBloodLust())) {
	      touchdown = ((game.getTeamHome().hasPlayer(ballCarrier) && FieldCoordinateBounds.ENDZONE_AWAY.isInBounds(ballPosition))
	        || (game.getTeamAway().hasPlayer(ballCarrier) && FieldCoordinateBounds.ENDZONE_HOME.isInBounds(ballPosition)));
	    }
	  }
	  return touchdown;
	}
	
	public static boolean checkEndOfHalf(GameState pGameState) {
		Game game = pGameState.getGame();
    return (game.getTurnDataHome().getTurnNr() >= 8) && (game.getTurnDataAway().getTurnNr() >= 8);
	}

}

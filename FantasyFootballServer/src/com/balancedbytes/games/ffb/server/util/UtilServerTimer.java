package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerTimer {
  
  public static void waitForOpponent(GameState pGameState, long currentTimeMillis, boolean pWaiting) {
    Game game = pGameState.getGame();
    game.setWaitingForOpponent(pWaiting);
    if (pWaiting) {
      stopTurnTimer(pGameState, currentTimeMillis);
    } else {
      startTurnTimer(pGameState, currentTimeMillis);
    }
  }

  public static void startTurnTimer(GameState gameState, long currentTimeMillis) {
    Game game = gameState.getGame();
    if ((gameState.getTurnTimeStarted() == 0) && game.isTurnTimeEnabled()) {
      gameState.setTurnTimeStarted(currentTimeMillis - game.getTurnTime());
    }
  }

  public static void stopTurnTimer(GameState gameState, long currentTimeMillis) {
    Game game = gameState.getGame();
    if ((gameState.getTurnTimeStarted() > 0) && game.isTurnTimeEnabled()) {
      game.setTurnTime(currentTimeMillis - gameState.getTurnTimeStarted());
    }
    gameState.setTurnTimeStarted(0);
  }

  public static void syncTime(GameState gameState, long currentTimeMillis) {
    Game game = gameState.getGame();
    if (game.getStarted() != null) {
      if (game.getFinished() == null) {
        game.setGameTime(currentTimeMillis - game.getStarted().getTime());
        if ((gameState.getTurnTimeStarted() > 0) && game.isTurnTimeEnabled()) {
          game.setTurnTime(currentTimeMillis - gameState.getTurnTimeStarted());
          if (!game.isTimeoutPossible() && (UtilGameOption.getIntOption(game, GameOptionId.TURNTIME) > 0)
            && (game.getTurnTime() >= UtilGameOption.getIntOption(game, GameOptionId.TURNTIME) * 1000)) {
            game.setTimeoutPossible(true);
          }
        }
      } else {
        game.setGameTime(game.getFinished().getTime() - game.getStarted().getTime());
      }
    }
  }
  
}

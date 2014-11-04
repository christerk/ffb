package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerTimer {
  
  public static void waitForOpponent(GameState pGameState, boolean pWaiting) {
    Game game = pGameState.getGame();
    game.setWaitingForOpponent(pWaiting);
    if (pWaiting) {
      stopTurnTimer(pGameState);
    } else {
      startTurnTimer(pGameState);
    }
  }

  public static void startTurnTimer(GameState pGameState) {
    Game game = pGameState.getGame();
    if (pGameState.getTurnTimeStarted() == 0) {
      long currentMillis = System.currentTimeMillis();
      pGameState.setTurnTimeStarted(currentMillis - game.getTurnTime());
      // System.out.println("startTurnTimer(" + game.getTurnTime() + ")");
    }
  }

  public static void stopTurnTimer(GameState pGameState) {
    if (pGameState.getTurnTimeStarted() > 0) {
      Game game = pGameState.getGame();
      long currentMillis = System.currentTimeMillis();
      game.setTurnTime(currentMillis - pGameState.getTurnTimeStarted());
      // System.out.println("stopTurnTimer(" + game.getTurnTime() + ")");
      pGameState.setTurnTimeStarted(0);
    }
  }

  public static void syncTime(GameState pGameState) {
    Game game = pGameState.getGame();
    if (game.getStarted() != null) {
      if (game.getFinished() == null) {
        long currentMillis = System.currentTimeMillis();
        game.setGameTime(currentMillis - game.getStarted().getTime());
        if ((pGameState.getTurnTimeStarted() > 0) && game.isTurnTimeEnabled()) {
          game.setTurnTime(currentMillis - pGameState.getTurnTimeStarted());
        }
      } else {
        game.setGameTime(game.getFinished().getTime() - game.getStarted().getTime());
      }
    }
  }
  
}

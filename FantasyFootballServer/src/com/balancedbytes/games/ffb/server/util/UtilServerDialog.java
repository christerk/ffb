package com.balancedbytes.games.ffb.server.util;


import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerDialog {
  
  public static void showDialog(GameState gameState, IDialogParameter dialogParameter, boolean stopTurnTimer) {
    Game game = gameState.getGame();
    game.setDialogParameter(dialogParameter);
    game.setWaitingForOpponent(stopTurnTimer);
    if (stopTurnTimer) {
      UtilServerTimer.stopTurnTimer(gameState, System.currentTimeMillis());;
    }
  }

  public static void hideDialog(GameState gameState) {
    Game game = gameState.getGame();
    if (game.getDialogParameter() != null) {
      game.setDialogParameter(null);
      UtilServerTimer.startTurnTimer(gameState, System.currentTimeMillis());
    }
  }

}

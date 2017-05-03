package com.balancedbytes.games.ffb.server.util;


import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerDialog {
  
  public static void showDialog(GameState gameState, IDialogParameter dialogParameter) {
    Game game = gameState.getGame();
    game.setDialogParameter(dialogParameter);
    UtilServerTimer.waitForOpponent(
      gameState,
      System.currentTimeMillis(),
      (dialogParameter != null) && dialogParameter.getId().isWaitingDialog()
    );
  }

  public static void hideDialog(GameState gameState) {
    Game game = gameState.getGame();
    if (game.getDialogParameter() != null) {
      showDialog(gameState, null);
    }
  }

}

package com.balancedbytes.games.ffb.server.util;


import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class UtilServerDialog {
  
  public static void showDialog(GameState pGameState, IDialogParameter pDialogParameter) {
    Game game = pGameState.getGame();
    game.setDialogParameter(pDialogParameter);
    UtilServerTimer.waitForOpponent(pGameState, (pDialogParameter != null) && pDialogParameter.getId().isWaitingDialog());
  }

  public static void hideDialog(GameState pGameState) {
    Game game = pGameState.getGame();
    if (game.getDialogParameter() != null) {
      showDialog(pGameState, null);
    }
  }

}

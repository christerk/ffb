package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilGameOption {
  
  public static boolean isOptionEnabled(Game pGame, GameOptionId pOptionId) {
    if ((pGame == null) || (pOptionId == null)) {
      return false;
    }
    GameOptionBoolean gameOption = (GameOptionBoolean) pGame.getOptions().getOption(pOptionId);
    if (gameOption == null) {
      gameOption = (GameOptionBoolean) new GameOptionFactory().createGameOption(pOptionId);
    }
    return gameOption.isEnabled();
  }
  
  public static int getIntOption(Game pGame, GameOptionId pOptionId) {
    if ((pGame == null) || (pOptionId == null)) {
      return 0;
    }
    GameOptionInt gameOption = (GameOptionInt) pGame.getOptions().getOption(pOptionId);
    if (gameOption == null) {
      gameOption = (GameOptionInt) new GameOptionFactory().createGameOption(pOptionId);
    }
    return gameOption.getValue();
  }

}

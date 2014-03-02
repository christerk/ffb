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
    return ((GameOptionBoolean) pGame.getOptions().getOption(pOptionId)).isEnabled();
  }
  
  public static int getIntOption(Game pGame, GameOptionId pOptionId) {
    if ((pGame == null) || (pOptionId == null)) {
      return 0;
    }
    return ((GameOptionInt) pGame.getOptions().getOption(pOptionId)).getValue();
  }

}

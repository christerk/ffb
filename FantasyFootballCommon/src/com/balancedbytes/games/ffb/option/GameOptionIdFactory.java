package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionIdFactory implements IEnumWithNameFactory {
  
  public GameOptionId forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (GameOptionId optionId : GameOptionId.values()) {
        if (pName.equalsIgnoreCase(optionId.getName())) {
          return optionId;
        }
      }
    }
    return null;
  }

}

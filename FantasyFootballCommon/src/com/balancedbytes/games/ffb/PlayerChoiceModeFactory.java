package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PlayerChoiceModeFactory implements IEnumWithNameFactory {
  
  public PlayerChoiceMode forName(String pName) {
    for (PlayerChoiceMode type : PlayerChoiceMode.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}

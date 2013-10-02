package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class PlayerChoiceModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public PlayerChoiceMode forName(String pName) {
    for (PlayerChoiceMode type : PlayerChoiceMode.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public PlayerChoiceMode forId(int pId) {
    for (PlayerChoiceMode type : PlayerChoiceMode.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }
  
}

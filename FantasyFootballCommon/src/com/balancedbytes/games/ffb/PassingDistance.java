package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum PassingDistance implements IEnumWithId, IEnumWithName {
  
  QUICK_PASS(1, "Quick Pass", 1, 'Q'),
  SHORT_PASS(2, "Short Pass", 0, 'S'),
  LONG_PASS(3, "Long Pass", -1, 'L'),
  LONG_BOMB(4, "Long Bomb", -2, 'B');

  private int fId;
  private String fName;
  private int fModifier;
  private char fShortcut;
  
  private PassingDistance(int pId, String pName, int pModifier, char pShortcut) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fShortcut = pShortcut;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public char getShortcut() {
    return fShortcut;
  }

}

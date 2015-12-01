package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum PassingDistance implements IEnumWithName {
  
  QUICK_PASS("Quick Pass", 1, 'Q'),
  SHORT_PASS("Short Pass", 0, 'S'),
  LONG_PASS("Long Pass", -1, 'L'),
  LONG_BOMB("Long Bomb", -2, 'B');

  private String fName;
  private int fModifier;
  private char fShortcut;
  
  private PassingDistance(String pName, int pModifier, char pShortcut) {
    fName = pName;
    fModifier = pModifier;
    fShortcut = pShortcut;
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

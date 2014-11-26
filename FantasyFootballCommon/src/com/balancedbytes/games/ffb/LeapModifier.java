package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum LeapModifier implements IRollModifier {
  
  // TODO: create factory for this
  
  VERY_LONG_LEGS(1, "Very Long Legs", -1);
  
  private int fId;
  private String fName;
  private int fModifier;
  
  private LeapModifier(int pId, String pName, int pModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
  }
  
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isModifierIncluded() {
    return false;
  }
  
}

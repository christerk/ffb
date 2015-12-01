package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum LeapModifier implements IRollModifier {
  
  // TODO: create factory for this
  
  VERY_LONG_LEGS("Very Long Legs", -1);
  
  private String fName;
  private int fModifier;
  
  private LeapModifier(String pName, int pModifier) {
    fName = pName;
    fModifier = pModifier;
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

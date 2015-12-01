package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum GoForItModifier implements IRollModifier {
  
  BLIZZARD("Blizzard", 1),
  GREASED_SHOES("Greased Shoes", 3);
  
  private String fName;
  private int fModifier;
  
  private GoForItModifier(String pName, int pModifier) {
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

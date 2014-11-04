package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum GoForItModifier implements IRollModifier {
  
  BLIZZARD(1, "Blizzard", 1),
  GREASED_SHOES(2, "Greased Shoes", 3);
  
  private int fId;
  private String fName;
  private int fModifier;
  
  private GoForItModifier(int pId, String pName, int pModifier) {
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

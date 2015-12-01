package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum PickupModifier implements IRollModifier {
  
  BIG_HAND("Big Hand", 0, false),
  POURING_RAIN("Pouring Rain", 1, false),
  EXTRA_ARMS("Extra Arms", -1, false),
  TACKLEZONES_1("1 Tacklezone", 1, true),
  TACKLEZONES_2("2 Tacklezones", 2, true),
  TACKLEZONES_3("3 Tacklezones", 3, true),
  TACKLEZONES_4("4 Tacklezones", 4, true),
  TACKLEZONES_5("5 Tacklezones", 5, true),
  TACKLEZONES_6("6 Tacklezones", 6, true),
  TACKLEZONES_7("7 Tacklezones", 7, true),
  TACKLEZONES_8("8 Tacklezones", 8, true);
  
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  
  private PickupModifier(String pName, int pModifier, boolean pTacklezoneModifier) {
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
  }
    
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }

  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
    
  public boolean isModifierIncluded() {
    return isTacklezoneModifier();
  }
  
}

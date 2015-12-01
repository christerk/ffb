package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum InjuryModifier implements IEnumWithName {
  
  MIGHTY_BLOW("Mighty Blow", 1, false),
  DIRTY_PLAYER("Dirty Player", 1, false),
  STUNTY("Stunty", 0, false),
  THICK_SKULL("Thick Skull", 0, false),
  NIGGLING_INJURIES_1("1 Niggling Injury", 1, true),
  NIGGLING_INJURIES_2("2 Niggling Injuries", 2, true),
  NIGGLING_INJURIES_3("3 Niggling Injuries", 3, true),
  NIGGLING_INJURIES_4("4 Niggling Injuries", 4, true),
  NIGGLING_INJURIES_5("5 Niggling Injuries", 5, true);

  private String fName;
  private int fModifier;
  private boolean fNigglingInjuryModifier;
  
  private InjuryModifier(String pName, int pModifier, boolean pNigglingInjuryModifier) {
    fName = pName;
    fModifier = pModifier;
    fNigglingInjuryModifier = pNigglingInjuryModifier;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isNigglingInjuryModifier() {
    return fNigglingInjuryModifier;
  }
    
}

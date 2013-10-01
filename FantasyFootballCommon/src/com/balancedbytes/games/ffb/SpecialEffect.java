package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum SpecialEffect {
  
  LIGHTNING(1, "lightning", true),
  FIREBALL(2, "fireball", true),
  BOMB(3, "bomb", false);
  
  private int fId;
  private String fName;
  private boolean fWizardSpell;
  
  private SpecialEffect(int pValue, String pName, boolean pWizardSpell) {
    fId = pValue;
    fName = pName;
    fWizardSpell = pWizardSpell;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isWizardSpell() {
		return fWizardSpell;
	}
  
  public static SpecialEffect fromId(int pId) {
    for (SpecialEffect spell : values()) {
      if (spell.getId() == pId) {
        return spell;
      }
    }
    return null;
  }
    
  public static SpecialEffect fromName(String pName) {
    for (SpecialEffect spell : values()) {
      if (spell.getName().equalsIgnoreCase(pName)) {
        return spell;
      }
    }
    return null;
  }
  
}

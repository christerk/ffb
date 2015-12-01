package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum SpecialEffect implements IEnumWithName {
  
  LIGHTNING("lightning", true),
  FIREBALL("fireball", true),
  BOMB("bomb", false);
  
  private String fName;
  private boolean fWizardSpell;
  
  private SpecialEffect(String pName, boolean pWizardSpell) {
    fName = pName;
    fWizardSpell = pWizardSpell;
  }

  public String getName() {
    return fName;
  }
  
  public boolean isWizardSpell() {
		return fWizardSpell;
	}
  
}

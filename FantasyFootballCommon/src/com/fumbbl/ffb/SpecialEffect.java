package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum SpecialEffect implements INamedObject {

	LIGHTNING("lightning", true), FIREBALL("fireball", true), ZAP("zap", true), BOMB("bomb", false);

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

package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum CardEffect implements INamedObject {

	DISTRACTED("Distracted"), ILLEGALLY_SUBSTITUTED("IllegallySubstituted"),
	MAD_CAP_MUSHROOM_POTION("MadCapMushroomPotion"), SEDATIVE("Sedative"), POISONED("Poisoned");

	private String fName;

	private CardEffect(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}

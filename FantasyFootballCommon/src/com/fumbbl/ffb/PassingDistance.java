package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PassingDistance implements INamedObject {

	QUICK_PASS("Quick Pass", 1, 0,'Q'),
	SHORT_PASS("Short Pass", 0, 1, 'S'),
	LONG_PASS("Long Pass", -1, 2, 'L'),
	LONG_BOMB("Long Bomb", -2, 3, 'B');

	private String fName;
	private int modifier2016, modifier2020;
	private char fShortcut;

	PassingDistance(String pName, int modifier2016, int modifier2020, char pShortcut) {
		fName = pName;
		this.modifier2016 = modifier2016;
		this.modifier2020 = modifier2020;
		fShortcut = pShortcut;
	}

	public String getName() {
		return fName;
	}

	public int getModifier2016() {
		return modifier2016;
	}

	public int getModifier2020() {
		return modifier2020;
	}

	public char getShortcut() {
		return fShortcut;
	}

}

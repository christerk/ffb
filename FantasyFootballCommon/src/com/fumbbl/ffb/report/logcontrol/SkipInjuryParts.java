package com.fumbbl.ffb.report.logcontrol;

public enum SkipInjuryParts {
	ARMOUR(true, false), ARMOUR_AND_INJURY(true, true), NONE(false, false);

	private final boolean armour;
	private final boolean injury;

	SkipInjuryParts(boolean armour, boolean injury) {
		this.armour = armour;
		this.injury = injury;
	}

	public boolean isArmour() {
		return armour;
	}

	public boolean isInjury() {
		return injury;
	}
}

package com.fumbbl.ffb.report.logcontrol;

public enum SkipInjuryParts {
	ARMOUR(true, false),
	ARMOUR_AND_CAS(true, false, true),
	ARMOUR_AND_INJURY(true, true),
	EVERYTHING_BUT_CAS(true, true, false),
	INJURY(false, true),
	CAS(false, false, true),
	NONE(false, false);

	private final boolean armour;
	private final boolean injury;
	private final boolean cas;

	SkipInjuryParts(boolean armour, boolean injury) {
		this(armour, injury, injury);
	}

	SkipInjuryParts(boolean armour, boolean injury, boolean cas) {
		this.armour = armour;
		this.injury = injury;
		this.cas = cas;
	}

	public boolean isArmour() {
		return armour;
	}

	public boolean isInjury() {
		return injury;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isCas() {
		return cas;
	}
}

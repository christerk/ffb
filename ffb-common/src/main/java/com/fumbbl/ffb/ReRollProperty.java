package com.fumbbl.ffb;

public enum ReRollProperty implements INamedObject {
	TRR(true), BRILLIANT_COACHING, MASCOT(true), PRO(true), LONER, PUMP_UP_THE_CROWD,
	SHOW_STAR;

	private final boolean actualReRoll;

	ReRollProperty() {
		this(false);
	}

	ReRollProperty(boolean actualReRoll) {
		this.actualReRoll = actualReRoll;
	}

	@Override
	public String getName() {
		return name();
	}

	public boolean isActualReRoll() {
		return actualReRoll;
	}
}

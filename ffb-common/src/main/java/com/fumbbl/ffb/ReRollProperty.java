package com.fumbbl.ffb;

public enum ReRollProperty implements INamedObject {
	TRR(true), SINGLE_USE, MASCOT(true), PRO(true), LONER, LEADER;

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

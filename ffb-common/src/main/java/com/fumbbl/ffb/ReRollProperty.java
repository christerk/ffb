package com.fumbbl.ffb;

public enum ReRollProperty implements INamedObject {
	TRR(true), BRILLIANT_COACHING, MASCOT(true), PRO(true), LONER, PUMP_UP_THE_CROWD,
	SHOW_STAR, BRAWLER(true), ANY_DIE_RE_ROLL(true), UNSTOPPABLE_MOMENTUM(true), SAVAGE_BLOW(true);

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

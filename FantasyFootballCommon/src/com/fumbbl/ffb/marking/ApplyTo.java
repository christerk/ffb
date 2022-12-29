package com.fumbbl.ffb.marking;

public enum ApplyTo {
	OWN(true, false), OPPONENT(false, true), BOTH(true, true);

	private final boolean appliesToOwn;
	private final boolean appliesToOpponent;

	ApplyTo(boolean appliesToOwn, boolean appliesToOpponent) {
		this.appliesToOwn = appliesToOwn;
		this.appliesToOpponent = appliesToOpponent;
	}

	public boolean isAppliesToOwn() {
		return appliesToOwn;
	}

	public boolean isAppliesToOpponent() {
		return appliesToOpponent;
	}
}

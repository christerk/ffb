package com.fumbbl.ffb;

public enum ReRollProperty implements INamedObject {
	TRR, BRILLIANT_COACHING, MASCOT, PRO, LONER;

	@Override
	public String getName() {
		return name();
	}
}

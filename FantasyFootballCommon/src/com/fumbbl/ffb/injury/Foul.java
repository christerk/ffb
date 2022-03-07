package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Foul extends InjuryType {

	public Foul() {
		this("foul");
	}

	public Foul(String name) {
		super(name, false, SendToBoxReason.FOULED);
	}

	@Override
	public boolean shouldPlayFallSound() {
		return false;
	}

	@Override
	public boolean isFoul() {
		return true;
	}

}

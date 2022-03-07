package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class FoulForSpp extends InjuryType {

	public FoulForSpp() {
		this("foulForSpp");
	}

	public FoulForSpp(String name) {
		super(name, true, SendToBoxReason.FOULED);
	}

	@Override
	public boolean shouldPlayFallSound() {
		return false;
	}

	@Override
	public boolean isFoul() {
		return true;
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}
}

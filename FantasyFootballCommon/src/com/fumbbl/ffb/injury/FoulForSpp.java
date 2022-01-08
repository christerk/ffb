package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class FoulForSpp extends InjuryType {

	public FoulForSpp() {
		super("foul", true, SendToBoxReason.FOULED);
		// TODO Auto-generated constructor stub
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

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BreatheFireForSpp extends InjuryType {

	public BreatheFireForSpp() {
		super("breatheFireForSpp", true, SendToBoxReason.BREATHE_FIRE);
	}

	@Override
	public boolean isVomitLike() {
		return true;
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}
}

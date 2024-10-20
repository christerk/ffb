package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class QuickBite extends InjuryType {

	public QuickBite() {
		super("quickBite", false, SendToBoxReason.QUICK_BITE);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BreatheFire extends InjuryType {

	public BreatheFire() {
		super("breatheFire", false, SendToBoxReason.BREATHE_FIRE);
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

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class EatPlayer extends InjuryType {

	public EatPlayer() {
		super("eatPlayer", false, SendToBoxReason.EATEN);
	}

	@Override
	public boolean canUseApo() {
		return false;
	}

}

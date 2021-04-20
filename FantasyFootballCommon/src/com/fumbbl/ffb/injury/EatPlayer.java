package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class EatPlayer extends InjuryType {

	public EatPlayer() {
		super("eatPlayer", false, SendToBoxReason.EATEN);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canUseApo() {
		return false;
	}

}

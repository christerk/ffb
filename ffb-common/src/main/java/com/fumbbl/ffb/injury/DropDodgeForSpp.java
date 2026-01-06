package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class DropDodgeForSpp extends InjuryType {

	public DropDodgeForSpp() {
		super("dropDodgeForSpp", true, SendToBoxReason.DODGE_FAIL);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class TTMHitPlayerForSpp extends InjuryType {

	public TTMHitPlayerForSpp() {
		super("ttmHitPlayerForSpp", true, SendToBoxReason.HIT_BY_THROWN_PLAYER);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class TTMHitPlayer extends InjuryType {

	public TTMHitPlayer() {
		super("ttmHitPlayer", true, SendToBoxReason.HIT_BY_THROWN_PLAYER);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

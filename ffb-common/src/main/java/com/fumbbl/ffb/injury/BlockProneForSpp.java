package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockProneForSpp extends InjuryType {

	public BlockProneForSpp() {
		super("blockProneForSpp", true, SendToBoxReason.BLOCKED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

	@Override
	public boolean isBlock() {
		return true;
	}
}

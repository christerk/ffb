package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockStunned extends InjuryType {

	public BlockStunned() {
		super("blockStunned", false, SendToBoxReason.BLOCKED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

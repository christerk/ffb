package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockStunned extends InjuryType {

	public BlockStunned() {
		super("blockStunned", false, SendToBoxReason.BLOCKED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

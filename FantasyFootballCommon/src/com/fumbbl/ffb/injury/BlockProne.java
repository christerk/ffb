package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockProne extends InjuryType {

	public BlockProne() {
		super("blockProne", false, SendToBoxReason.BLOCKED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockProne extends InjuryType {

	public BlockProne() {
		super("blockProne", false, SendToBoxReason.BLOCKED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

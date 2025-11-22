package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Block extends InjuryType {

	public Block() {
		super("block", true, SendToBoxReason.BLOCKED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

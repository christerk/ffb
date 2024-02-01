package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Block extends InjuryType {

	public Block() {
		super("block", true, SendToBoxReason.BLOCKED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

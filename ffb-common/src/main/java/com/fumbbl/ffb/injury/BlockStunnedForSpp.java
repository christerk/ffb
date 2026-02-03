package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BlockStunnedForSpp extends InjuryType {

	public BlockStunnedForSpp() {
		super("blockStunnedForSpp", true, SendToBoxReason.BLOCKED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class ChainsawForSpp extends InjuryType {

	public ChainsawForSpp() {
		super("chainsawForSpp", true, SendToBoxReason.CHAINSAW);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

	@Override
	public boolean isChainsaw() {
		return true;
	}
}

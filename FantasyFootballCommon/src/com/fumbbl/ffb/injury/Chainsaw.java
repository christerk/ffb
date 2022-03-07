package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Chainsaw extends InjuryType {

	public Chainsaw() {
		super("chainsaw", false, SendToBoxReason.CHAINSAW);
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

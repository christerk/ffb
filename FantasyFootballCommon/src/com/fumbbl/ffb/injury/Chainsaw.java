package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class Chainsaw extends InjuryType {

	public Chainsaw() {
		super("chainsaw", false, SendToBoxReason.CHAINSAW);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

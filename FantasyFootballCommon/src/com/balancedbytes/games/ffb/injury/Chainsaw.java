package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

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

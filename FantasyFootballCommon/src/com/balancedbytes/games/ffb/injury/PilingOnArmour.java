package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

public class PilingOnArmour extends InjuryType {

	public PilingOnArmour() {
		super("pilingOnArmor", true, SendToBoxReason.PILED_ON);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

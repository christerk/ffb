package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

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

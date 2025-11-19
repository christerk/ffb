package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class PilingOnArmour extends InjuryType {

	public PilingOnArmour() {
		super("pilingOnArmor", true, SendToBoxReason.PILED_ON);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

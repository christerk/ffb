package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class PilingOnInjury extends InjuryType {

	public PilingOnInjury() {
		super("pilingOnInjury", true, SendToBoxReason.PILED_ON);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

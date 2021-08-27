package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class PilingOnInjury extends InjuryType {

	public PilingOnInjury() {
		super("pilingOnInjury", true, SendToBoxReason.PILED_ON);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

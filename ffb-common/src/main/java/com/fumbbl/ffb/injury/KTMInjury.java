package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class KTMInjury extends InjuryType {

	public KTMInjury() {
		super("ktmInjury", false, SendToBoxReason.KICKED);
	}

}

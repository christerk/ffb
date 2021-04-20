package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class TTMLanding extends InjuryType {

	public TTMLanding() {
		super("ttmLanding", false, SendToBoxReason.LANDING_FAIL);
	}

}

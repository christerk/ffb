package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Lightning extends InjuryType {

	public Lightning() {
		super("lightning", false, SendToBoxReason.LIGHTNING);
	}

}

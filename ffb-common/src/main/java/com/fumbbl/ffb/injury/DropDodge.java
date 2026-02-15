package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class DropDodge extends InjuryType {

	public DropDodge() {
		super("dropDodge", false, SendToBoxReason.DODGE_FAIL);
	}

}

package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class DropGFI extends InjuryType {

	public DropGFI() {
		super("dropGfi", false, SendToBoxReason.GFI_FAIL);
	}

}

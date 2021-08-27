package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class DropJump extends InjuryType {

	public DropJump() {
		super("dropLeap", false, SendToBoxReason.JUMP_FAIL);
		// TODO Auto-generated constructor stub
	}

}

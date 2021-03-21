package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

public class DropJump extends InjuryType {

	public DropJump() {
		super("dropLeap", false, SendToBoxReason.JUMP_FAIL);
		// TODO Auto-generated constructor stub
	}

}

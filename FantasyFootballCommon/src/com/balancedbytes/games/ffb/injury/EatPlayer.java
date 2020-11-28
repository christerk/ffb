package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

public class EatPlayer extends InjuryType  {

	public EatPlayer() {
		super("eatPlayer", false, SendToBoxReason.EATEN);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canUseApo() {
		return false;
	}

}

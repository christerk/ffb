package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

public class PilingOnKnockedOut extends InjuryType{

	public PilingOnKnockedOut() {
		super("pilingOnKnockedOut", false, SendToBoxReason.KO_ON_PILING_ON);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canUseApo() {
		return false;
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

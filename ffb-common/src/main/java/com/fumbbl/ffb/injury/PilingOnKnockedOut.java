package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class PilingOnKnockedOut extends InjuryType {

	public PilingOnKnockedOut() {
		super("pilingOnKnockedOut", false, SendToBoxReason.KO_ON_PILING_ON);
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

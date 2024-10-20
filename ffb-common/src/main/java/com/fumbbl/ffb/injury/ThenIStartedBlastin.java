package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class ThenIStartedBlastin extends InjuryType {

	public ThenIStartedBlastin() {
		super("startedBlastin", false, SendToBoxReason.THEN_I_STARTED_BLASTIN);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

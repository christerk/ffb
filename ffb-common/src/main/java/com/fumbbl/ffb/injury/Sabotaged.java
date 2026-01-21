package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Sabotaged extends InjuryType {

	public Sabotaged() {
		super("sabotaged", false, SendToBoxReason.SABOTAGED);
	}

	@Override
	public boolean isCausedByOpponent() {
		return false;
	}
}

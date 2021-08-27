package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;

public class Foul extends InjuryType {

	public Foul() {
		super("foul", false, SendToBoxReason.FOULED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldPlayFallSound() {
		return false;
	}

	@Override
	public boolean isFoul() {
		return true;
	}

}

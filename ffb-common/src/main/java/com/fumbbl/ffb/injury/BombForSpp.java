package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class BombForSpp extends InjuryType {

	public BombForSpp() {
		super("bombForSpp", true, SendToBoxReason.BOMB);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}

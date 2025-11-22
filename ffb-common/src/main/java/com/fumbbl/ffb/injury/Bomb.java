package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Bomb extends InjuryType {

	public Bomb() {
		super("bomb", false, SendToBoxReason.BOMB);
	}

}

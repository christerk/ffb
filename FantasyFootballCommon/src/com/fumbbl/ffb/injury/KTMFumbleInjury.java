package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class KTMFumbleInjury extends InjuryType {

	public KTMFumbleInjury() {
		super("ktmFumbleInjury", false, SendToBoxReason.KICKED);
	}

	@Override
	public boolean canApoKoIntoStun() {
		return false;
	}
}

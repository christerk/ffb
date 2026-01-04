package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class KTMFumbleApoKoInjury extends InjuryType {
	
	public KTMFumbleApoKoInjury() {
		super("ktmFumbleApoKoInjury", false, SendToBoxReason.KICKED);
	}

	@Override
	public boolean canApoKoIntoStun() {
		return true;
	}
}

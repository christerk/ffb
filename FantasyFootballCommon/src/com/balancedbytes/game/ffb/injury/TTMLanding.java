package com.balancedbytes.game.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;

public class TTMLanding extends InjuryType {

	public TTMLanding() {
		super("ttmLanding", false, SendToBoxReason.LANDING_FAIL);
	}

}

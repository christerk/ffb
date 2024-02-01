package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class KegHit extends InjuryType {
	public KegHit() {
		super("kegHit", false, SendToBoxReason.THROWN_KEG);
	}
}

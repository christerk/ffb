package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class KTMCrowd extends InjuryType {

	public KTMCrowd() {
		super("ktmCrowd", false, SendToBoxReason.CROWD_KICKED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		string.append(defender.getName());
		string.append(" is kicked into the crowd and is knocked out.");
	}
}

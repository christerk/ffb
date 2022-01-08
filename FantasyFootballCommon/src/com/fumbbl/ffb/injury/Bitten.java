package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class Bitten extends InjuryType {

	public Bitten() {
		super("bitten", false, SendToBoxReason.BITTEN);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		string.append(attacker.getName());
		string.append(" bites ");
		string.append(defender.getName());
	}

}

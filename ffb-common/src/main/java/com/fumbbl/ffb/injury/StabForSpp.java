package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class StabForSpp extends InjuryType {

	public StabForSpp() {
		super("stabForSpp", true, SendToBoxReason.STABBED);
	}

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		if (attacker != null) {
			string.append(attacker.getName());
			string.append(" stabs ");
			string.append(defender.getName());
		} else {
			string.append(defender.getName());
			string.append(" is stabbed");
		}
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

	@Override
	public boolean isStab() {
		return true;
	}

}

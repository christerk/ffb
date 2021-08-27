package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class Stab extends InjuryType {

	public Stab() {
		super("stab", false, SendToBoxReason.STABBED);
		// TODO Auto-generated constructor stub
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

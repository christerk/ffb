package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class BallAndChain extends InjuryType {

	public BallAndChain() {
		super("ballAndChain", false, SendToBoxReason.BALL_AND_CHAIN);
	}

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		string.append(defender.getName());
		string.append(" is knocked out by ");
		string.append(defender.getPlayerGender().getGenitive());
		string.append(" own Ball & Chain.");
	}

	@Override
	public boolean isBlock() {
		return true;
	}
}

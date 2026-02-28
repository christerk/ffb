package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.model.Player;

public class BallAndChainBlockKnockDown extends Block {

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		string.append(defender.getName());
		string.append(" is at least knocked out by ");
		string.append(defender.getPlayerGender().getGenitive());
		string.append(" own Ball & Chain.");
	}
}

package com.balancedbytes.game.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Player;

public class KTMCrowd extends InjuryType {

	public KTMCrowd() {
		super("ktmCrowd", false, SendToBoxReason.CROWD_KICKED);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender)
	{
		string.append(defender.getName());
		string.append(" is kicked into the crowd and is knocked out.");
	}
}

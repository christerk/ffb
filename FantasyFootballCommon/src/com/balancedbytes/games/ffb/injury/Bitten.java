package com.balancedbytes.games.ffb.injury;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Player;

public class Bitten extends InjuryType  {

	public Bitten() {
		super("bitten", false, SendToBoxReason.BITTEN);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender)
	{
		string.append(attacker.getName());
		string.append(" bites ");
		string.append(defender.getName());
	}

}

package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {
	@Override
	public boolean eligibleToPass(Player<?> player) {
		return player.getPassing() > 0 && !player.hasSkillWithProperty(NamedProperties.preventRegularPassAction);
	}
}

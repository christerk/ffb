package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {
	@Override
	public boolean eligibleToPass(Player<?> player) {
		return !player.hasSkillWithProperty(NamedProperties.preventRegularPassAction);
	}
}

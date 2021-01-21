package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Player;

import java.util.Collection;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {

	@Override
	public int minimumRoll(Player<?> pThrower, PassingDistance pPassingDistance,
	                       Collection<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return Math.max(Math.max(2 - (pPassingDistance.getModifier2016() - modifierTotal), 2),
			7 - Math.min(pThrower.getAgility(), 6) - pPassingDistance.getModifier2016() + modifierTotal);
	}
}

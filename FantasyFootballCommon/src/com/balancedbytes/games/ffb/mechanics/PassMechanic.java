package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

import java.util.Collection;

public abstract class PassMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.PASS;
	}

	public boolean eligibleToPass(Player<?> player) {
		return !player.hasSkillWithProperty(NamedProperties.preventRegularPassAction);
	}

	public abstract int minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers);

}

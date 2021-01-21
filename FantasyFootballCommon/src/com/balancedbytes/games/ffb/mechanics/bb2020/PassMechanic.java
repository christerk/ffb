package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

import java.util.Collection;

public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {
	@Override
	public boolean eligibleToPass(Player<?> player) {
		return player.getPassing() > 0 && !player.hasSkillWithProperty(NamedProperties.preventRegularPassAction);
	}

	@Override
	public int minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers) {
		return thrower.getPassing() + distance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum();
	}
}

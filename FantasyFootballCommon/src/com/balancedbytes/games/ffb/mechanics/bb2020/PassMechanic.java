package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;

import java.util.Collection;

public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {

	@Override
	public int minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers) {
		return thrower.getPassing() + distance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum();
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		int minimumRoll = minimumRoll(thrower, distance, modifiers);
		if (roll == 1) {
			return PassResult.FUMBLE;
		} else if (roll == 6) {
			return PassResult.ACCURATE;
		} else if (isModifiedFumble(roll, distance, modifiers)) {
			return PassResult.WILDLY_INACCURATE;
		} else {

		}
		return null;
	}

	@Override
	public boolean isModifiedFumble(int roll, PassingDistance pPassingDistance, Collection<PassModifier> pPassModifiers) {
		int modifierTotal = calculateModifiers(pPassModifiers);
		return roll - modifierTotal - pPassingDistance.getModifier2020() <= 1;
	}
}

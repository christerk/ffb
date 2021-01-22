package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

import java.util.Collection;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {

	@Override
	public int minimumRoll(Player<?> pThrower, PassingDistance pPassingDistance,
	                       Collection<PassModifier> pPassModifiers) {
		int modifierTotal = calculateModifiers(pPassModifiers);
		return Math.max(Math.max(2 - (pPassingDistance.getModifier2016() - modifierTotal), 2),
			7 - Math.min(pThrower.getAgility(), 6) - pPassingDistance.getModifier2016() + modifierTotal);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		int minimumRoll = minimumRoll(thrower, distance, modifiers);

		if (roll == 6) {
			return PassResult.ACCURATE;
		} else if (roll == 1) {
			return PassResult.FUMBLE;
		} else if (isModifiedFumble(roll, distance, modifiers)) {
			if (thrower.hasSkillWithProperty(NamedProperties.dontDropFumbles) && !bombAction) {
				return PassResult.SAVED_FUMBLE;
			} else {
				return PassResult.FUMBLE;
			}
		} else if (roll < minimumRoll) {
			return PassResult.INACCURATE;
		} else {
			return PassResult.ACCURATE;
		}
	}

	public boolean isModifiedFumble(int roll, PassingDistance distance, Collection<PassModifier> modifiers) {
		return ((roll + distance.getModifier2016() - calculateModifiers(modifiers)) <= 1);
	}
}

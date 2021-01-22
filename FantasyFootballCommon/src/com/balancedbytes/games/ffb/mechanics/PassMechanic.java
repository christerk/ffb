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

	public abstract PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction);

	public boolean isModifiedFumble(int roll, PassingDistance pPassingDistance, Collection<PassModifier> pPassModifiers) {
		return isBelow(roll, pPassingDistance, calculateModifiers(pPassModifiers), 1);
	}
	
	protected int calculateModifiers(Collection<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return modifierTotal;
	}

	protected abstract boolean isBelow(int roll, PassingDistance distance, int modifiers, int threshold);
}

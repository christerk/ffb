package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.model.Player;

import java.util.Collection;
import java.util.Optional;

public abstract class PassMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.PASS;
	}

	public abstract Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers);

	public abstract PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction);

	protected int calculateModifiers(Collection<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return modifierTotal;
	}

	public abstract String formatReportRoll(int roll, Player<?> thrower);

	public abstract String formatRollRequirement(PassingDistance distance, String formattedModifiers, Player<?> thrower);

	public abstract boolean eligibleToReRoll(ReRolledAction reRolledAction, Player<?> thrower);
}

package com.balancedbytes.games.ffb.mechanics.bb2016;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

import java.util.Collection;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {

	@Override
	public Optional<Integer> minimumRoll(Player<?> pThrower, PassingDistance pPassingDistance,
	                                     Collection<PassModifier> pPassModifiers) {
		int minimumRoll = minimumRollInternal(pThrower, pPassingDistance, pPassModifiers);
		return Optional.of(minimumRoll);
	}

	private int minimumRollInternal(Player<?> pThrower, PassingDistance pPassingDistance, Collection<PassModifier> pPassModifiers) {
		int modifierTotal = calculateModifiers(pPassModifiers);
		return Math.max(Math.max(2 - (pPassingDistance.getModifier2016() - modifierTotal), 2),
			7 - Math.min(pThrower.getAgility(), 6) - pPassingDistance.getModifier2016() + modifierTotal);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		int minimumRoll = minimumRollInternal(thrower, distance, modifiers);

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

	@Override
	public String formatRollRequirement(PassingDistance distance, String formattedModifiers, Player<?> thrower) {
		StringBuilder rollRequirement = new StringBuilder();
		rollRequirement.append(" (AG").append(Math.min(6, thrower.getAgility()));
		if (distance.getModifier2016() >= 0) {
			rollRequirement.append(" + ");
		} else {
			rollRequirement.append(" - ");
		}
		rollRequirement.append(Math.abs(distance.getModifier2016())).append(" ").append(distance.getName());
		rollRequirement.append(formattedModifiers).append(" + Roll > 6).");
		return rollRequirement.toString();
	}

	@Override
	public boolean eligibleToReRoll(ReRolledAction reRolledAction, Player<?> thrower) {
		return reRolledAction != ReRolledActions.PASS;
	}

	public boolean isModifiedFumble(int roll, PassingDistance distance, Collection<PassModifier> modifiers) {
		return ((roll + distance.getModifier2016() - calculateModifiers(modifiers)) <= 1);
	}

	@Override
	public String formatReportRoll(int roll, Player<?> thrower) {
		return "Pass Roll [ " + roll + " ]";
	}
}

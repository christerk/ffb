package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.PassModifier;

import java.util.Collection;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2020)
public class PassMechanic extends com.balancedbytes.games.ffb.mechanics.PassMechanic {

	@Override
	protected String[] throwingRangeTable() {
		return new String[] {
			"T Q Q Q S S S L L L L B B B",
			"Q Q Q Q S S S L L L L B B B",
			"Q Q Q S S S S L L L L B B B",
			"Q Q S S S S S L L L B B B  ",
			"S S S S S S L L L L B B B  ",
			"S S S S S L L L L B B B    ",
			"S S S S L L L L L B B B    ",
			"L L L L L L L L B B B      ",
			"L L L L L L L B B B B      ",
			"L L L L L B B B B B        ",
			"L L L B B B B B B          ",
			"B B B B B B B              ",
			"B B B B B                  ",
			"B B B                      " };

	}

	@Override
	public Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers) {
		if (thrower.getPassingWithModifiers() > 0) {
			return Optional.of(minimumRollInternal(thrower, distance, modifiers));
		} else {
			return Optional.empty();
		}
	}

	private int minimumRollInternal(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers) {
		int roll = thrower.getPassingWithModifiers() + distance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum();
		return Math.max(roll, 2);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		if (thrower.getPassingWithModifiers() <= 0) {
			return PassResult.FUMBLE;
		}
		int minimumRoll = minimumRollInternal(thrower, distance, modifiers);
		if (roll == 1) {
			if (thrower.hasSkillProperty(NamedProperties.dontDropFumbles) && !bombAction) {
				return PassResult.SAVED_FUMBLE;
			} else {
				return PassResult.FUMBLE;
			}
		} else if (roll == 6) {
			return PassResult.ACCURATE;
		} else if (isModifiedFumble(roll, distance, modifiers)) {
			return PassResult.WILDLY_INACCURATE;
		} else if (roll < minimumRoll) {
			return PassResult.INACCURATE;
		} else {
			return PassResult.ACCURATE;
		}
	}

	@Override
	public String formatRollRequirement(PassingDistance distance, String formattedModifiers, Player<?> thrower) {
		if (thrower.getPassingWithModifiers() <= 0) {
			return "";
		}
		return " (Roll - " + distance.getModifier2020() + " " + distance.getName() + formattedModifiers +
			" >= PA " + thrower.getPassingWithModifiers() + "+).";
	}

	@Override
	public boolean eligibleToReRoll(ReRolledAction reRolledAction, Player<?> thrower) {
		return reRolledAction != ReRolledActions.PASS && thrower.getPassingWithModifiers() > 0;
	}

	public boolean isModifiedFumble(int roll, PassingDistance pPassingDistance, Collection<PassModifier> pPassModifiers) {
		int modifierTotal = calculateModifiers(pPassModifiers);
		return roll - modifierTotal - pPassingDistance.getModifier2020() <= 1;
	}

	@Override
	public String formatReportRoll(int roll, Player<?> thrower) {
		if (thrower.getPassingWithModifiers() > 0) {
			return "Pass Roll [ " + roll + " ]";
		}
		return "Pass fumbled automatically as " + thrower.getName() + " has no Passing Ability";
	}

}

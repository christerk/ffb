package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;

import java.util.Collection;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassMechanic extends com.fumbbl.ffb.mechanics.PassMechanic {

	@Override
	protected String[] throwingRangeTable() {
		return new String[] {
			"T Q Q Q S S S L L L L B B B",
			"Q Q Q Q S S S L L L L B B B",
			"Q Q Q S S S S L L L L B B  ",
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
			"B B                        "};

	}

	@Override
	public Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers, StatBasedRollModifier statBasedRollModifier) {
		return minimumRoll(thrower, distance, modifiers);
	}

	@Override
	public Optional<Integer> minimumRoll(Player<?> pThrower, PassingDistance pPassingDistance,
																			 Collection<PassModifier> pPassModifiers) {
		int minimumRoll = minimumRollInternal(pThrower, pPassingDistance, pPassModifiers);
		return Optional.of(minimumRoll);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction, StatBasedRollModifier statBasedRollModifier) {
		return evaluatePass(thrower, roll, distance, modifiers, bombAction);
	}

	private int minimumRollInternal(Player<?> pThrower, PassingDistance pPassingDistance, Collection<PassModifier> pPassModifiers) {
		int modifierTotal = calculateModifiers(pPassModifiers);
		return Math.max(Math.max(2 - (pPassingDistance.getModifier2016() - modifierTotal), 2),
			7 - Math.min(pThrower.getAgilityWithModifiers(), 6) - pPassingDistance.getModifier2016() + modifierTotal);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		int minimumRoll = minimumRollInternal(thrower, distance, modifiers);

		if (roll == 6) {
			return PassResult.ACCURATE;
		} else if (roll == 1) {
			return PassResult.FUMBLE;
		} else if (isModifiedFumble(roll, distance, modifiers)) {
			if (thrower.hasSkillProperty(NamedProperties.dontDropFumbles) && !bombAction) {
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
		rollRequirement.append(" (AG").append(Math.min(6, thrower.getAgilityWithModifiers()));
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

	private boolean isModifiedFumble(int roll, PassingDistance distance, Collection<PassModifier> modifiers) {
		return ((roll + distance.getModifier2016() - calculateModifiers(modifiers)) <= 1);
	}

	@Override
	public String formatReportRoll(int roll, Player<?> thrower) {
		return "Pass Roll [ " + roll + " ]";
	}

}

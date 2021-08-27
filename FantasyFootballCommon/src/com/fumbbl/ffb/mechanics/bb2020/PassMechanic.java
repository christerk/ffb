package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;

import java.util.Collection;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2020)
public class PassMechanic extends com.fumbbl.ffb.mechanics.PassMechanic {

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
			int roll = thrower.getPassingWithModifiers() + distance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum();
			return Optional.of(Math.max(roll, 2));
		} else {
			return Optional.empty();
		}
	}

 	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {

	  int resultAfterModifiers = roll - calculateModifiers(modifiers) - distance.getModifier2020();
	  if (thrower.getPassingWithModifiers() <= 0 || roll == 1) {
		  if (thrower.hasSkillProperty(NamedProperties.dontDropFumbles) && !bombAction) {
			  return PassResult.SAVED_FUMBLE;
		  } else {
			  return PassResult.FUMBLE;
		  }
	  } else if (roll == 6 || resultAfterModifiers >= thrower.getPassingWithModifiers()) {
		  return PassResult.ACCURATE;
	  } else if (resultAfterModifiers <= 1) {
		  return PassResult.WILDLY_INACCURATE;
	  } else {
			return PassResult.INACCURATE;
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

	@Override
	public String formatReportRoll(int roll, Player<?> thrower) {
		if (thrower.getPassingWithModifiers() > 0) {
			return "Pass Roll [ " + roll + " ]";
		}
		return "Pass fumbled automatically as " + thrower.getName() + " has no Passing Ability";
	}

}

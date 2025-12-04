package com.fumbbl.ffb.mechanics.mixed;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
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
	public Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers, StatBasedRollModifier statBasedRollModifier) {
		if (thrower.getPassingWithModifiers() > 0) {
			int roll = thrower.getPassingWithModifiers() + distance.getModifier2020() + modifiers.stream().mapToInt(PassModifier::getModifier).sum();
			if (statBasedRollModifier != null) {
				roll += statBasedRollModifier.getModifier();
			}
			return Optional.of(Math.max(roll, 2));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers) {
		return minimumRoll(thrower, distance, modifiers, null);
	}

	@Override
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers,
																 boolean bombAction, StatBasedRollModifier statBasedRollModifier) {

		int resultAfterModifiers = roll - calculateModifiers(modifiers) - distance.getModifier2020();
		if (statBasedRollModifier != null) {
			resultAfterModifiers += statBasedRollModifier.getModifier();
		}
		if (thrower.getPassingWithModifiers() <= 0 || roll == 1) {
			if (thrower.hasSkillProperty(NamedProperties.dontDropFumbles)) {
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
	public PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction) {
		return evaluatePass(thrower, roll, distance, modifiers, bombAction, null);
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
	public int passModifiers(Game game, Player<?> player) {
		Player<?>[] players = UtilPlayer.findTacklezonePlayers(game, player);
		int zones = players.length;

		ActingPlayer actingPlayer = game.getActingPlayer();
		if (game.getTurnMode() == TurnMode.DUMP_OFF
			&& Arrays.stream(players).anyMatch(adjacentPlayer -> adjacentPlayer.getId().equals(actingPlayer.getPlayerId()))
			&& actingPlayer.isStandingUp()
		) {
			zones -= 1;
		}

		return zones;
	}

	@Override
	public String formatReportRoll(int roll, Player<?> thrower) {
		if (thrower.getPassingWithModifiers() > 0) {
			return "Pass Roll [ " + roll + " ]";
		}
		return "Pass fumbled automatically as " + thrower.getName() + " has no Passing Ability";
	}

}

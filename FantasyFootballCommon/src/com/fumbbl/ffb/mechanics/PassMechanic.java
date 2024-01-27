package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.PassingDistanceFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;

import java.util.Collection;
import java.util.Optional;

public abstract class PassMechanic implements Mechanic {

	@Override
	public Type getType() {
		return Type.PASS;
	}

	protected final PassingDistance[][] PASSING_DISTANCES_TABLE = new PassingDistance[14][14];

	public PassMechanic() {
		String[] throwingRangeTable = throwingRangeTable();
		PassingDistanceFactory passingDistanceFactory = new PassingDistanceFactory();
		for (int y = 0; y < 14; y++) {
			for (int x = 0; x < 14; x++) {
				PASSING_DISTANCES_TABLE[y][x] = passingDistanceFactory.forShortcut(throwingRangeTable[y].charAt(x * 2));
			}
		}
	}

	protected abstract String[] throwingRangeTable();

	public abstract Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers, StatBasedRollModifier statBasedRollModifier);

	public abstract Optional<Integer> minimumRoll(Player<?> thrower, PassingDistance distance, Collection<PassModifier> modifiers);

	public abstract PassResult evaluatePass(Player<?> thrower, int roll, PassingDistance distance, Collection<PassModifier> modifiers, boolean bombAction, StatBasedRollModifier statBasedRollModifier);

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

	public PassingDistance findPassingDistance(Game pGame, FieldCoordinate pFromCoordinate,
	                                           FieldCoordinate pToCoordinate, boolean pThrowTeamMate) {
		PassingDistance passingDistance = null;
		if ((pFromCoordinate != null) && (pToCoordinate != null)) {
			int deltaY = Math.abs(pToCoordinate.getY() - pFromCoordinate.getY());
			int deltaX = Math.abs(pToCoordinate.getX() - pFromCoordinate.getX());
			if ((deltaY < 14) && (deltaX < 14)) {
				passingDistance = PASSING_DISTANCES_TABLE[deltaY][deltaX];
			}
			if ((pThrowTeamMate || pGame.getFieldModel().getWeather().equals(Weather.BLIZZARD))
				&& ((passingDistance == PassingDistance.LONG_BOMB) || (passingDistance == PassingDistance.LONG_PASS))) {
				passingDistance = null;
			}
		}
		return passingDistance;
	}

	public abstract int passModifiers(Game game, Player<?> player);
}

package com.balancedbytes.games.ffb.util;

import java.util.Set;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class UtilRangeRuler {

	public static RangeRuler createRangeRuler(Game pGame, Player<?> pThrower, FieldCoordinate pTargetCoordinate,
			boolean pThrowTeamMate) {
		RangeRuler rangeRuler = null;
		if ((pGame != null) && (pThrower != null) && (pTargetCoordinate != null)) {
			PassMechanic mechanic = (PassMechanic) pGame.getRules().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
			FieldCoordinate throwerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pThrower);
			PassingDistance passingDistance = UtilPassing.findPassingDistance(pGame, throwerCoordinate, pTargetCoordinate,
					pThrowTeamMate);
			if (passingDistance != null) {
				int minimumRoll;
				Set<PassModifier> passModifiers = new PassModifierFactory().findPassModifiers(pGame, pThrower, passingDistance,
						pThrowTeamMate);
				if (pThrowTeamMate) {
					minimumRoll = minimumRollThrowTeamMate(passingDistance, passModifiers);
				} else {
					minimumRoll = mechanic.minimumRoll(pThrower, passingDistance, passModifiers);
				}
				rangeRuler = new RangeRuler(pThrower.getId(), pTargetCoordinate, minimumRoll, pThrowTeamMate);
			}
		}
		return rangeRuler;
	}

	public static int minimumRollThrowTeamMate(PassingDistance pPassingDistance,
	                                           Set<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return Math.max(2, 2 - pPassingDistance.getModifier2016() + modifierTotal);
	}

}

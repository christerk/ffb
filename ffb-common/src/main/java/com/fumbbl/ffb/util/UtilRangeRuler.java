package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

import java.util.Optional;
import java.util.Set;

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
			PassingDistance passingDistance = mechanic.findPassingDistance(pGame, throwerCoordinate, pTargetCoordinate,
					pThrowTeamMate);
			if (passingDistance != null) {
				Optional<Integer> minimumRoll;
				PassModifierFactory factory = pGame.getFactory(FactoryType.Factory.PASS_MODIFIER);
				Set<PassModifier> passModifiers = factory.findModifiers(new PassContext(pGame, pThrower, passingDistance,
					pThrowTeamMate));
				TtmMechanic ttmMechanic = (TtmMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
				if (pThrowTeamMate) {
					minimumRoll = Optional.of(ttmMechanic.minimumRoll(passingDistance, passModifiers));
				} else {
					minimumRoll = mechanic.minimumRoll(pThrower, passingDistance, passModifiers);
				}
				rangeRuler = new RangeRuler(pThrower.getId(), pTargetCoordinate, minimumRoll.orElse(0), pThrowTeamMate);
			}
		}
		return rangeRuler;
	}
}

package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.KTMInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Set;

public class InjuryTypeFumbledKtm extends InjuryTypeServer<KTMInjury> {

	public InjuryTypeFumbledKtm() {
		super(new KTMInjury());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		injuryContext.setArmorBroken(true);

		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(factory.getNigglingInjuryModifier(pDefender));

		Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul());
		injuryContext.addInjuryModifiers(injuryModifiers);

		setInjury(pDefender, gameState, diceRoller);

		return injuryContext;
	}

	@Override
	public boolean stunIsTreatedAsKo() {
		return true;
	}
}
package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ArmorModifierFactory;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeStab extends InjuryTypeServer<Stab> {
	public InjuryTypeStab() {
		super(new Stab());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(), isFoul());

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());

			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(null);
		}

		return injuryContext;
	}
}
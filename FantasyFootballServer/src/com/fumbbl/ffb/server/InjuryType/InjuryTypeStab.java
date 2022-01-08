package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Set;

public class InjuryTypeStab extends InjuryTypeServer<Stab> {
	private final boolean useInjuryModifiers;

	public InjuryTypeStab(boolean useInjuryModifiers) {
		super(new Stab());
		this.useInjuryModifiers = useInjuryModifiers;
		super.setFailedArmourPlacesProne(false);
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			Set<ArmorModifier> modifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(), isFoul());
			injuryContext.addArmorModifiers(modifiers);
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());

			if (useInjuryModifiers) {
				InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
				Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
					pDefender, isStab(), isFoul(), isVomit());
				injuryContext.addInjuryModifiers(injuryModifiers);
			}

			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(null);
		}

		return injuryContext;
	}
}
package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
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

import java.util.Set;

public class InjuryTypeStab extends ModificationAwareInjuryTypeServer<Stab> {
	private final boolean useInjuryModifiers;

	public InjuryTypeStab(boolean useInjuryModifiers) {
		super(new Stab());
		this.useInjuryModifiers = useInjuryModifiers;
		super.setFailedArmourPlacesProne(false);
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		if (useInjuryModifiers) {
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomit());
			injuryContext.addInjuryModifiers(injuryModifiers);
		}
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			Set<ArmorModifier> modifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(), isFoul());
			injuryContext.addArmorModifiers(modifiers);
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}
	}

	@Override
	protected void savedByArmour(InjuryContext injuryContext) {
		injuryContext.setInjury(null);
	}
}
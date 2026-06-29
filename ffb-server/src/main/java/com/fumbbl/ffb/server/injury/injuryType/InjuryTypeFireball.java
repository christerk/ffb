package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.Fireball;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.SpecialEffectArmourModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;

public class InjuryTypeFireball extends ModificationAwareInjuryTypeServer<Fireball> {
	public InjuryTypeFireball() {
		super(new Fireball());
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		if (!injuryContext.isArmorBroken()) {
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!injuryContext.isArmorBroken()) {
				((ArmorModifierFactory) game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER)).specialEffectArmourModifiers(SpecialEffect.FIREBALL, pDefender)
					.forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
		}
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker,
		Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate,
		InjuryContext injuryContext) {

		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));

		if (Arrays.stream(injuryContext.getArmorModifiers())
			.noneMatch(modifier -> modifier instanceof SpecialEffectArmourModifier)) {
			((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).specialEffectInjuryModifiers(SpecialEffect.FIREBALL)
				.forEach(injuryContext::addInjuryModifier);
		}

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}
}

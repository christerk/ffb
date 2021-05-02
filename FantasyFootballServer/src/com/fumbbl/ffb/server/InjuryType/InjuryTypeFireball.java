package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.Fireball;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.ArmorModifierFactory;
import com.fumbbl.ffb.modifiers.SpecialEffectArmourModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Arrays;

public class InjuryTypeFireball extends InjuryTypeServer<Fireball> {
	public InjuryTypeFireball() {
		super(new Fireball());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!injuryContext.isArmorBroken()) {
				((ArmorModifierFactory) game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER)).specialEffectArmourModifiers(SpecialEffect.FIREBALL)
					.forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));

			if (Arrays.stream(injuryContext.getArmorModifiers())
				.noneMatch(modifier -> modifier instanceof SpecialEffectArmourModifier)) {
				((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).specialEffectInjuryModifiers(SpecialEffect.FIREBALL)
					.forEach(injuryContext::addInjuryModifier);
			}
			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}
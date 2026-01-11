package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.BombForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.SpecialEffectArmourModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Arrays;

public class InjuryTypeBombWithModifierForSpp extends InjuryTypeServer<BombForSpp> {

	public InjuryTypeBombWithModifierForSpp() {
		super(new BombForSpp());
	}

	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                         Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                         ApothecaryMode pApothecaryMode) {

		// in BB2020 bombs place players prone, chainsaw only takes effect on falling down or being knocked down
		// hence chainsaw is ignored here
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
		injuryContext.setArmorRoll(diceRoller.rollArmour());
		injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

		if (!injuryContext.isArmorBroken()) {
			((ArmorModifierFactory) game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER)).specialEffectArmourModifiers(SpecialEffect.BOMB, pDefender)
				.forEach(injuryContext::addArmorModifier);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));

			if (Arrays.stream(injuryContext.getArmorModifiers())
				.noneMatch(modifier -> modifier instanceof SpecialEffectArmourModifier)) {
				((InjuryModifierFactory) game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).specialEffectInjuryModifiers(SpecialEffect.BOMB)
					.forEach(injuryContext::addInjuryModifier);
			}
			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}
	}
}
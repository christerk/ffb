package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.BreatheFire;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

import java.util.Set;

public class InjuryTypeBreatheFire extends ModificationAwareInjuryTypeServer<BreatheFire> {

	public InjuryTypeBreatheFire() {
		super(new BreatheFire());
		super.setFailedArmourPlacesProne(false);
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomitLike());
		injuryContext.addInjuryModifiers(injuryModifiers);

		setInjury(pDefender, gameState, diceRoller);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}
	}
}
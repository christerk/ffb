package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.BallAndChain;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeBallAndChain extends InjuryTypeServer<BallAndChain> {

	public InjuryTypeBallAndChain() {
		super(new BallAndChain());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {
		// ball and chain always breaks armour on being knocked down
		injuryContext.setArmorBroken(true);

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul()).forEach(injuryModifier -> injuryContext.addInjuryModifier(injuryModifier));
		setInjury(pDefender, gameState, diceRoller);

		return injuryContext;
	}

}
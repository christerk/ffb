package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.KTMInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeKTMInjury extends InjuryTypeServer<KTMInjury> {
	InjuryTypeKTMInjury() {
		super(new KTMInjury());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		injuryContext.setArmorBroken(true);

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));
		setInjury(pDefender, gameState, diceRoller);

		// Kick Team-Mate injuries get KO'd instead of Stunned
		if ((injuryContext.getInjury() != null) && injuryContext.getInjury().getBase() == PlayerState.STUNNED) {
			injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}

		return injuryContext;
	}
}
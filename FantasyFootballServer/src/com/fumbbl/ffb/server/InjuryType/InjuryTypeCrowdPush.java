package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.CrowdPush;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeCrowdPush extends InjuryTypeServer<CrowdPush> {
	public InjuryTypeCrowdPush() {
		super(new CrowdPush());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorBroken(true);
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));
		setInjury(pDefender, gameState, diceRoller);

		// crowdpush to reserve
		if (!injuryContext.isCasualty() && !injuryContext.isKnockedOut()) {
			injuryContext.setInjury(new PlayerState(PlayerState.RESERVE));
		}

		return injuryContext;
	}
}

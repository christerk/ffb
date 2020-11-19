package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeChainsaw extends InjuryTypeServer {
	public InjuryTypeChainsaw(IStep step) {
		super(step, "chainsaw", false, SendToBoxReason.CHAINSAW);
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}


	@Override
	public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.addArmorModifier(ArmorModifier.CHAINSAW);
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
			setInjury(pDefender);
		} else {
			injuryContext.setInjury(null);
		}
		return injuryContext;
	}
}
package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeKTMInjury extends InjuryTypeServer {
	InjuryTypeKTMInjury(IStep step) {
		super(step, "ktmInjury", false, SendToBoxReason.KICKED);
	}


	@Override
	public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorBroken(true);
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
		setInjury(pDefender);

		// Kick Team-Mate injuries get KO'd instead of Stunned
		if ((injuryContext.getInjury() != null) && injuryContext.getInjury().getBase() == PlayerState.STUNNED) {
			injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}

		return injuryContext;
	}
}
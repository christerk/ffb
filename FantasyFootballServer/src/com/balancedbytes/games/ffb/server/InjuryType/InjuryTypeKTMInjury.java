package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.game.ffb.injury.KTMInjury;
import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeKTMInjury extends InjuryTypeServer<KTMInjury>  {
	InjuryTypeKTMInjury() {
		super(new KTMInjury());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorBroken(true);
		}

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
		setInjury(pDefender, gameState, diceRoller);

		// Kick Team-Mate injuries get KO'd instead of Stunned
		if ((injuryContext.getInjury() != null) && injuryContext.getInjury().getBase() == PlayerState.STUNNED) {
			injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		}

		return injuryContext;
	}
}
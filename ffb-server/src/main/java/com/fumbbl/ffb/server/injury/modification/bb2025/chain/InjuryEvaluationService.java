package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;

public class InjuryEvaluationService {

	public boolean isArmourBroken(GameState gameState, InjuryContext injuryContext) {
		return DiceInterpreter.getInstance().isArmourBroken(gameState, injuryContext);
	}

	/**
	 * @return the injury for the current injury roll, <code>null</code> for a casualty that still needs a casualty roll
	 */
	public PlayerState interpretInjury(GameState gameState, InjuryContext injuryContext) {
		Game game = gameState.getGame();
		RollMechanic rollMechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.ROLL.name());
		return rollMechanic.interpretInjuryRoll(game, injuryContext);
	}
}

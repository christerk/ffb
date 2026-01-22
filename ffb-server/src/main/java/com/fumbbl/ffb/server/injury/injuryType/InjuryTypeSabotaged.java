package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.Sabotaged;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeSabotaged extends InjuryTypeServer<Sabotaged> {

	public InjuryTypeSabotaged() {
		super(new Sabotaged());
	}

	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                         Player<?> attacker, Player<?> defender, FieldCoordinate defenderCoordinate,
	                         FieldCoordinate fromCoordinate, InjuryContext oldContext, ApothecaryMode apothecaryMode) {

		// standard armour + injury roll, but no block modifiers
		injuryContext.setArmorRoll(diceRoller.rollArmour());
		injuryContext.setArmorBroken(DiceInterpreter.getInstance().isArmourBroken(gameState, injuryContext));

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			// no extra modifiers
			setInjury(defender, gameState, diceRoller, injuryContext);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}
	}
}

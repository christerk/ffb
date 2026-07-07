package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.injury.Sabotaged;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;

public class InjuryTypeSabotaged extends ModificationAwareInjuryTypeServer<Sabotaged> {

	public InjuryTypeSabotaged() {
		super(new Sabotaged());
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> attacker,
		Player<?> defender, FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate,
		DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {

		// standard armour + injury roll, but no block modifiers
		if (roll) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
		}
		injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> attacker,
		Player<?> defender, FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate,
		InjuryContext injuryContext) {

		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		// no extra modifiers
		setInjury(defender, gameState, diceRoller, injuryContext);
	}
}

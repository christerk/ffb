package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeBlockStunned extends InjuryTypeServer<BlockStunned> {
	public InjuryTypeBlockStunned() {
		super(new BlockStunned());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}
		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(((InjuryModifierFactory)game.getFactory(FactoryType.Factory.INJURY_MODIFIER)).getNigglingInjuryModifier(pDefender));
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.STUNNED));
		}

		return injuryContext;
	}
}
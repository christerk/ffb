package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContextForModification;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;

public class ReRollSingleArmourDie extends InjuryContextModification {

	public ReRollSingleArmourDie() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(Chainsaw.class);
			add(Foul.class);
			add(FoulForSpp.class);
		}});
	}

	@Override
	protected boolean modifyArmourInternal(InjuryContextForModification newContext, GameState gameState) {
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		int replaceIndex = newContext.fArmorRoll[0] < newContext.fArmorRoll[1] ? 0 : 1;
		int oldValue = newContext.fArmorRoll[replaceIndex];
		newContext.fArmorRoll[replaceIndex] = 6;
		newContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, newContext));

		if (!newContext.isArmorBroken()) {
			return false;
		}

		int newValue = gameState.getDiceRoller().rollDice(6);
		newContext.fArmorRoll[replaceIndex] = newValue;
		newContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, newContext));

		return true;
	}

	@Override
	public SkillUse skillUse() {
		return SkillUse.RE_ROLL_LOWER_ARMOUR_DIE;
	}
}

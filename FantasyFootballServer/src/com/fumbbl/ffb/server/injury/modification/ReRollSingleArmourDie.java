package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryContextForModification;
import com.fumbbl.ffb.report.bb2020.ReportOldPro;
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
	protected boolean tryArmourRollModification(InjuryContext injuryContext, InjuryType injuryType) {
		return super.tryArmourRollModification(injuryContext, injuryType)
			|| (injuryContext.fArmorRoll[0] == injuryContext.fArmorRoll[1] && injuryType.isFoul());
	}

	@Override
	protected boolean modifyArmourInternal(GameState gameState, InjuryContextForModification newContext, InjuryType injuryType) {
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		boolean spottedFoul = (newContext.fArmorRoll[0] == newContext.fArmorRoll[1] && injuryType.isFoul());
		int replaceIndex = newContext.fArmorRoll[0] < newContext.fArmorRoll[1] ? 0 : 1;
		int oldValue = newContext.fArmorRoll[replaceIndex];
		newContext.fArmorRoll[replaceIndex] = 6;
		newContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, newContext));

		if (!newContext.isArmorBroken() && !spottedFoul) {
			return false;
		}

		int newValue = gameState.getDiceRoller().rollDice(6);
		newContext.fArmorRoll[replaceIndex] = newValue;
		newContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, newContext));
		newContext.addReport(new ReportOldPro(newContext.fAttackerId, oldValue, newValue));

		return true;
	}

	@Override
	public SkillUse skillUse() {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}
}

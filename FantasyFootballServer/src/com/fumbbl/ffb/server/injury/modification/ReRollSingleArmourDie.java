package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.ProjectileVomit;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.report.bb2020.ReportOldPro;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;

public class ReRollSingleArmourDie extends InjuryContextModification<ReRollSingleArmourDieParams> {

	public ReRollSingleArmourDie() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(Chainsaw.class);
			add(Foul.class);
			add(FoulForSpp.class);
			add(BlockProne.class);
			add(BlockStunned.class);
			add(Stab.class);
			add(ProjectileVomit.class);
		}});
	}

	@Override
	protected ReRollSingleArmourDieParams getParams(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ReRollSingleArmourDieParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(InjuryContext injuryContext, InjuryType injuryType) {
		return super.tryArmourRollModification(injuryContext, injuryType)
			|| (injuryContext.fArmorRoll[0] == injuryContext.fArmorRoll[1] && injuryType.isFoul());
	}


	@Override
	protected void prepareArmourParams(ReRollSingleArmourDieParams params) {
		ModifiedInjuryContext newContext = params.getNewContext();
		params.setSpottedFoul(newContext.fArmorRoll[0] == newContext.fArmorRoll[1] && params.getInjuryType().isFoul());
		params.setReplaceIndex(newContext.fArmorRoll[0] < newContext.fArmorRoll[1] ? 0 : 1);
		params.setOldValue(newContext.fArmorRoll[params.getReplaceIndex()]);
		newContext.fArmorRoll[params.getReplaceIndex()] = 6;
		newContext.setArmorBroken(params.getDiceInterpreter().isArmourBroken(params.getGameState(), newContext));
	}

	@Override
	protected boolean armourModificationCantHelp(ReRollSingleArmourDieParams params) {
		return !params.isSpottedFoul() && !params.getNewContext().isArmorBroken();
	}

	@Override
	protected void applyArmourModification(ReRollSingleArmourDieParams params) {
		int newValue = params.getGameState().getDiceRoller().rollDice(6);
		ModifiedInjuryContext newContext = params.getNewContext();
		newContext.fArmorRoll[params.getReplaceIndex()] = newValue;
		newContext.addReport(new ReportOldPro(newContext.fAttackerId, params.getOldValue(), newValue));
	}

	@Override
	public SkillUse skillUse() {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}
}

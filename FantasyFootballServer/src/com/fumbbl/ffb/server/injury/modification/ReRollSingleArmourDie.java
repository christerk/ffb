package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.ProjectileVomit;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.report.bb2020.ReportOldPro;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashSet;

public class ReRollSingleArmourDie extends InjuryContextModification<ReRollSingleArmourDieParams> {

	public ReRollSingleArmourDie() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(Chainsaw.class);
			add(Foul.class);
			add(FoulForSpp.class);
			add(FoulForSppWithChainsaw.class);
			add(FoulWithChainsaw.class);
			add(BlockProne.class);
			add(BlockStunned.class);
			add(Stab.class);
			add(ProjectileVomit.class);
		}});
	}

	@Override
	protected ReRollSingleArmourDieParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ReRollSingleArmourDieParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(ReRollSingleArmourDieParams params) {
		params.setSpottedFoul(isSpottedFoul(params.getNewContext(), params.getInjuryType()));
		params.setSelfInflicted(isSelfInflicted(params.getNewContext(), params.getInjuryType()));
		return super.tryArmourRollModification(params) != params.isSelfInflicted()
			|| params.isSpottedFoul();
	}

	private boolean isSpottedFoul(InjuryContext injuryContext, InjuryType injuryType) {
		return injuryContext.fArmorRoll[0] == injuryContext.fArmorRoll[1] && injuryType.isFoul();
	}

	private boolean isSelfInflicted(InjuryContext injuryContext, InjuryType injuryType) {
		return (!StringTool.isProvided(injuryContext.fAttackerId) && (injuryType.isVomit() || injuryType.isChainsaw())) || injuryContext.getApothecaryMode() == ApothecaryMode.ANIMAL_SAVAGERY;
	}

	@Override
	protected void prepareArmourParams(ReRollSingleArmourDieParams params) {
		ModifiedInjuryContext newContext = params.getNewContext();
		params.setSpottedFoul(newContext.fArmorRoll[0] == newContext.fArmorRoll[1] && params.getInjuryType().isFoul());
		params.setReplaceIndex(newContext.fArmorRoll[0] < newContext.fArmorRoll[1] != params.isSelfInflicted() ? 0 : 1);
		params.setOldValue(newContext.fArmorRoll[params.getReplaceIndex()]);
		newContext.fArmorRoll[params.getReplaceIndex()] = params.isSelfInflicted() ? 1 : 6;
		newContext.setArmorBroken(params.getDiceInterpreter().isArmourBroken(params.getGameState(), newContext));
	}

	@Override
	protected boolean armourModificationCantHelp(ReRollSingleArmourDieParams params) {
		return !params.isSpottedFoul() && (params.getNewContext().isArmorBroken() == params.isSelfInflicted());
	}

	@Override
	protected void applyArmourModification(ReRollSingleArmourDieParams params) {
		int newValue = params.getGameState().getDiceRoller().rollDice(6);
		ModifiedInjuryContext newContext = params.getNewContext();
		newContext.fArmorRoll[params.getReplaceIndex()] = newValue;
		String playerId = params.isSelfInflicted() ? newContext.fDefenderId : newContext.fAttackerId;
		newContext.addReport(new ReportOldPro(playerId, params.getOldValue(), newValue, params.isSelfInflicted()));
	}

	@Override
	public SkillUse skillUse() {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}
}

package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class RerollArmourChainModification extends ChainableInjuryModification {

	protected RerollArmourChainModification(Set<Class<? extends InjuryType>> validInjuryTypes,
		boolean validForAllInjuryTypes, InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(validInjuryTypes, validForAllInjuryTypes, evaluationService, copyService);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return Collections.singleton(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_ARMOUR;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		return !state.getInjuryContext().isArmorBroken();
	}

	@Override
	protected void applyToArmour(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		InjuryContext injuryContext = state.getInjuryContext();
		injuryContext.setArmorRoll(gameState.getDiceRoller().rollArmour());
		injuryContext.setArmorBroken(evaluationService.isArmourBroken(gameState, injuryContext));
	}
}

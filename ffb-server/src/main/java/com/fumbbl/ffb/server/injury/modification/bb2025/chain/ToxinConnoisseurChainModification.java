package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.StabForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToxinConnoisseurChainModification extends ChainableInjuryModification {

	public ToxinConnoisseurChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	ToxinConnoisseurChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(new HashSet<Class<? extends InjuryType>>(Arrays.asList(Stab.class, StabForSpp.class)), false,
			evaluationService, copyService);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return Collections.singleton(InjuryPhase.INJURY);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.MODIFIER;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.ADD_INJURY_MODIFIER;
	}

	@Override
	protected boolean isApplicableToInjury(GameState gameState, InjuryResolutionState state) {
		InjuryContext injuryContext = state.getInjuryContext();
		return !isCasualty(injuryContext) && changesInjuryWith(gameState, injuryContext, skillInjuryModifiers());
	}

	@Override
	protected void applyToInjury(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		addInjuryModifiers(gameState, state, skillInjuryModifiers());
	}
}

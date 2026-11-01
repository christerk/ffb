package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class DwarfenGritChainModification extends RerollArmourChainModification {

	public DwarfenGritChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	DwarfenGritChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(Collections.<Class<? extends InjuryType>>emptySet(), true, evaluationService, copyService);
	}

	@Override
	protected boolean ownedByDefender() {
		return true;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		return state.getInjuryContext().isArmorBroken();
	}

	@Override
	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return true;
	}
}

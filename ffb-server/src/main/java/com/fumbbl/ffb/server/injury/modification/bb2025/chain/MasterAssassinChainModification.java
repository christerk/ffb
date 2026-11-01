package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;

import java.util.Collections;

public class MasterAssassinChainModification extends RerollArmourChainModification {

	public MasterAssassinChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	MasterAssassinChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(Collections.<Class<? extends InjuryType>>singleton(Stab.class), false, evaluationService, copyService);
	}
}

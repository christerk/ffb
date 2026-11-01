package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;

import java.util.Collections;

public class KrumpAndSmashChainModification extends RerollArmourChainModification {

	public KrumpAndSmashChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	KrumpAndSmashChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(Collections.<Class<? extends InjuryType>>singleton(Block.class), false, evaluationService, copyService);
	}
}

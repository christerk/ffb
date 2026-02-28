package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.injury.BallAndChainBlockKnockDown;

public class InjuryTypeBallAndChainBlockKnockDown
	extends DispatchingBlockInjuryType<BallAndChainBlockKnockDown> {

	public InjuryTypeBallAndChainBlockKnockDown() {
		this(BlockInjuryEvaluator.Mode.REGULAR, true);
	}

	public InjuryTypeBallAndChainBlockKnockDown(BlockInjuryEvaluator.Mode mode) {
		this(mode, true);
	}

	public InjuryTypeBallAndChainBlockKnockDown(BlockInjuryEvaluator.Mode mode, boolean allowAttackerChainsaw) {
		super(new BallAndChainBlockKnockDown(), mode, allowAttackerChainsaw);
	}
}

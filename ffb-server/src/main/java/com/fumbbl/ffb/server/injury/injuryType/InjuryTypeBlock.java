package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.injury.Block;

public class InjuryTypeBlock extends DispatchingBlockInjuryType<Block> {

	public InjuryTypeBlock() {
		this(BlockInjuryEvaluator.Mode.REGULAR, true);
	}

	public InjuryTypeBlock(BlockInjuryEvaluator.Mode mode) {
		this(mode, true);
	}

	public InjuryTypeBlock(BlockInjuryEvaluator.Mode mode, boolean allowAttackerChainsaw) {
		super(new Block(), mode, allowAttackerChainsaw);
	}

}
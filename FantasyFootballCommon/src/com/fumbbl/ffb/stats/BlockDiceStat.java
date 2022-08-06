package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.BlockResult;

public class BlockDiceStat extends DieStat<BlockResult[]>{
	public BlockDiceStat(String id, BlockResult[] value) {
		super(DieBase.BLOCK, TeamMapping.TEAM_FOR_PLAYER, id, value);
	}

}

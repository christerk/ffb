package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.BlockResult;

public class BlockDiceStat extends DieStat<int[]>{
	public BlockDiceStat(String id, int[] value) {
		super(DieBase.BLOCK, TeamMapping.TEAM_FOR_PLAYER, id, value);
	}

}

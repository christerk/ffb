package com.fumbbl.ffb.stats;

import java.util.List;

public class DicePoolStat extends DieStat<List<Integer>>{

	public DicePoolStat(DieBase base, TeamMapping mapping, String id, List<Integer> value) {
		super(base, mapping, id, value);
	}

	public DicePoolStat(DieBase base, TeamMapping mapping, String id, List<Integer> value, boolean duringGame) {
		super(base, mapping, id, value, duringGame);
	}
}

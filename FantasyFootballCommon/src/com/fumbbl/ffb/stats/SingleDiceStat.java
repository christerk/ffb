package com.fumbbl.ffb.stats;

import java.util.List;

public class SingleDiceStat extends DieStat<List<Integer>>{

	public SingleDiceStat(DieBase base, TeamMapping mapping, String id, List<Integer> value) {
		super(base, mapping, id, value);
	}

	public SingleDiceStat(DieBase base, TeamMapping mapping, String id, List<Integer> value, boolean duringGame) {
		super(base, mapping, id, value, duringGame);
	}
}

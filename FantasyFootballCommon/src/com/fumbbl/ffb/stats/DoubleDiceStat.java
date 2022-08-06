package com.fumbbl.ffb.stats;

public class DoubleDiceStat extends DieStat<int[]> {


	public DoubleDiceStat(DieBase base, TeamMapping mapping, String id, int[] value) {
		super(base, mapping, id, value);
	}

	public DoubleDiceStat(DieBase base, TeamMapping mapping, String id, int[] value, boolean duringGame) {
		super(base, mapping, id, value, duringGame);
	}
}

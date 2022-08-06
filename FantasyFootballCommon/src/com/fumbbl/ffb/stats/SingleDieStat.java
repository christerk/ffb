package com.fumbbl.ffb.stats;

public class SingleDieStat extends DieStat<Integer>{

	private final int minimumRoll;

	public SingleDieStat(DieBase base, TeamMapping mapping, String id, Integer value, int minimumRoll) {
		super(base, mapping, id, value);
		this.minimumRoll = minimumRoll;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}
}

package com.fumbbl.ffb.stats;

public class SkillDieStat extends DieStat<Integer>{

	private final int minimumRoll;

	public SkillDieStat(DieBase base, TeamMapping mapping, String id, Integer value, int minimumRoll) {
		super(base, mapping, id, value);
		this.minimumRoll = minimumRoll;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}
}

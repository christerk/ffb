package com.fumbbl.ffb.modifiers;

public class PlayerStatLimit {

	private final int max;
	private final int min;

	public PlayerStatLimit(int min, int max) {
		this.max = max;
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}
}

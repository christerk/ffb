package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.StatsMechanic;

public class TemporaryStatIncrementer extends TemporaryStatModifier {
	public TemporaryStatIncrementer(PlayerStatKey stat, StatsMechanic mechanic) {
		super(stat, mechanic);
	}

	@Override
	public int apply(int value) {
		return value + 1;
	}

}

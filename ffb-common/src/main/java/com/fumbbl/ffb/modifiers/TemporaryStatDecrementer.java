package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.StatsMechanic;

public class TemporaryStatDecrementer extends TemporaryStatModifier {
	public TemporaryStatDecrementer(PlayerStatKey stat, StatsMechanic mechanic) {
		super(stat, mechanic);
	}

	@Override
	public int apply(int value) {
		return value - 1;
	}

}

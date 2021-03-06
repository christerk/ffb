package com.balancedbytes.games.ffb.modifiers;

public class TemporaryStatDecrementer extends TemporaryStatModifier {
	public TemporaryStatDecrementer(PlayerStat stat) {
		super(stat);
	}

	@Override
	public int apply(int value) {
		return value - 1;
	}

}

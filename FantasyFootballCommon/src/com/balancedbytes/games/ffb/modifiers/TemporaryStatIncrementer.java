package com.balancedbytes.games.ffb.modifiers;

public class TemporaryStatIncrementer extends TemporaryStatModifier {
	public TemporaryStatIncrementer(PlayerStat stat) {
		super(stat);
	}

	@Override
	public int apply(int value) {
		return value + 1;
	}

}

package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;

public abstract class TemporaryStatModifier implements INamedObject {
	public static final String NAME_SEPARATOR = "-";

	protected final PlayerStat stat;

	@Override
	public String getName() {
		return stat.name() + NAME_SEPARATOR + getClass().getCanonicalName();
	}

	public TemporaryStatModifier(PlayerStat stat) {
		this.stat = stat;
	}

	public boolean appliesTo(PlayerStat stat) {
		return this.stat == stat;
	}

	public abstract int apply(int value);

	public enum PlayerStat {
		MA, ST, AG, PA, AV;
	}
}

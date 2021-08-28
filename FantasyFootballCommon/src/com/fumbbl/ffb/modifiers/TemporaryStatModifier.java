package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.mechanics.StatsMechanic;

public abstract class TemporaryStatModifier implements INamedObject {
	public static final String NAME_SEPARATOR = "-";

	protected final PlayerStatKey key;
	protected final PlayerStatLimit limit;

	public TemporaryStatModifier(PlayerStatKey key, StatsMechanic mechanic) {
		this.key = key;
		this.limit = mechanic.limit(key);
	}

	@Override
	public String getName() {
		return key.name() + NAME_SEPARATOR + getClass().getCanonicalName();
	}

	public boolean appliesTo(PlayerStatKey stat) {
		return this.key == stat;
	}

	public abstract int apply(int value);

	public PlayerStatLimit getLimit() {
		return limit;
	}

	public enum PlayerStatKey {
		MA, ST, AG, PA, AV
	}

}

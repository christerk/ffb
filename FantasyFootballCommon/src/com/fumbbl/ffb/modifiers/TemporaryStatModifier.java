package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.INamedObject;

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
		MA(1, 9), ST(1, 8), AG(1, 6), PA(1, 6), AV(3, 11);

		private final int max;
		private final int min;

		PlayerStat() {
			this(0, 0);
		}

		PlayerStat(int min, int max) {
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
}

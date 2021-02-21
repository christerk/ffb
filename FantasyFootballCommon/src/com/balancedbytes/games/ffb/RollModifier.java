package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.modifiers.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierContext;
//TODO either get rid of this class and sort equals/hashcode in some other way or remove IRollModifier
public abstract class RollModifier<C extends ModifierContext> implements IRollModifier<C> {
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == this.getClass()
			&& ((IRollModifier<?>)other).getName().equals(getName());
	}
}

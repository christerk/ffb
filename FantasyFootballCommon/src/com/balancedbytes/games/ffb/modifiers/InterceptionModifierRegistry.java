package com.balancedbytes.games.ffb.modifiers;

public abstract class InterceptionModifierRegistry extends ModifierRegistry<InterceptionContext, InterceptionModifier> {
	@Override
	public String getKey() {
		return "InterceptionModifierRegistry";
	}
}

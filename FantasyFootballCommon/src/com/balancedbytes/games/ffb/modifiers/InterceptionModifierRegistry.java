package com.balancedbytes.games.ffb.modifiers;

public abstract class InterceptionModifierRegistry extends ModifierRegistry<InterceptionModifierKey, InterceptionContext, InterceptionModifier> {
	@Override
	public String getKey() {
		return "InterceptionModifierRegistry";
	}
}

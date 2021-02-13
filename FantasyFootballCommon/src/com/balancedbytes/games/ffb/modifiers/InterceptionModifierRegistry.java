package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Map;

public interface InterceptionModifierRegistry extends ModifierRegistry, Map<com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey, com.balancedbytes.games.ffb.modifiers.InterceptionModifier> {
	@Override
	default String getKey() {
		return "InterceptionModifierRegistry";
	}
}

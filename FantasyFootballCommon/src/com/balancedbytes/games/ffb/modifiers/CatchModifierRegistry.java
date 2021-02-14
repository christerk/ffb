package com.balancedbytes.games.ffb.modifiers;

public abstract class CatchModifierRegistry extends ModifierRegistry<CatchModifierKey, CatchContext, CatchModifier> {

	@Override
	public String getKey() {
		return "CatchModifierRegistry";
	}

}

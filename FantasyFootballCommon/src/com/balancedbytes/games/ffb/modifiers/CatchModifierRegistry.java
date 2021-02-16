package com.balancedbytes.games.ffb.modifiers;

public abstract class CatchModifierRegistry extends ModifierRegistry<CatchContext, CatchModifier> {

	@Override
	public String getKey() {
		return "CatchModifierRegistry";
	}

}

package com.balancedbytes.games.ffb.modifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CatchModifierRegistry implements ModifierRegistry {

	protected final Map<CatchModifierKey, CatchModifier> values = new HashMap<>();

	@Override
	public String getKey() {
		return "CatchModifierRegistry";
	}

	public Optional<CatchModifier> get(CatchModifierKey key) {
		return Optional.ofNullable(values.get(key));
	}

	public Collection<CatchModifier> values() {
		return values.values();
	};

	protected void add(CatchModifier modifier) {
		values.put(modifier.getModifierKey(), modifier);
	}
}

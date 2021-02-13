package com.balancedbytes.games.ffb.modifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class InterceptionModifierRegistry implements ModifierRegistry {

	protected final Map<InterceptionModifierKey, InterceptionModifier> values = new HashMap<>();

	@Override
	public String getKey() {
		return "InterceptionModifierRegistry";
	}

	public Optional<InterceptionModifier> get(InterceptionModifierKey key) {
		return Optional.ofNullable(values.get(key));
	}

	public Collection<InterceptionModifier> values() {
		return values.values();
	};

	protected void add(InterceptionModifier modifier) {
		values.put(modifier.getModifierKey(), modifier);
	}
}

package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ModifierRegistry<C extends ModifierContext, V extends IRollModifier<C>> implements IKeyedItem {
	protected final Map<String, V> values = new HashMap<>();

	public Optional<V> get(String key) {
		return Optional.ofNullable(values.get(key));
	}

	public Collection<V> values() {
		return values.values();
	};

	protected void add(V modifier) {
		values.put(modifier.getName(), modifier);
	}
}

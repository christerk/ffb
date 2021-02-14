package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ModifierRegistry<K extends ModifierKey, V extends IRollModifier<K>> implements IKeyedItem {
	protected final Map<K, V> values = new HashMap<>();

	public Optional<V> get(K key) {
		return Optional.ofNullable(values.get(key));
	}

	public Collection<V> values() {
		return values.values();
	};

	protected void add(V modifier) {
		values.put(modifier.getModifierKey(), modifier);
	}
}

package com.fumbbl.ffb.modifiers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fumbbl.ffb.IKeyedItem;

public abstract class ModifierCollection<C extends ModifierContext, V extends RollModifier<C>> implements IKeyedItem {
	private final Set<V> modifiers = new HashSet<>();

	@Override
	public String getKey() {
		return getClass().getSimpleName();
	}

	protected void add(V modifier) {
		modifiers.add(modifier);
	}

	public Set<V> getModifiers(ModifierType type) {
		return modifiers.stream().filter(modifier -> modifier.getType() == type).collect(Collectors.toSet());
	}

	public Set<V> getModifiers() {
		return modifiers;
	}
}

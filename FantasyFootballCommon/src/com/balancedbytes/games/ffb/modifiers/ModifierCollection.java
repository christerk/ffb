package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class ModifierCollection<C extends ModifierContext, V extends IRollModifier<C>> implements IKeyedItem {
	private final Set<V> tacklezoneModifiers = new HashSet<>();
	private final Set<V> disturbingPresenceModifiers = new HashSet<>();

	private final Map<String, V> otherModifiers = new HashMap<>();

	public Optional<V> get(String key) {
		return Optional.ofNullable(otherModifiers.get(key));
	}

	public Collection<V> getOtherModifiers() {
		return otherModifiers.values();
	};

	protected void add(V modifier) {
		if (modifier.isTacklezoneModifier()) {
			tacklezoneModifiers.add(modifier);
		} else if (modifier.isDisturbingPresenceModifier()) {
			disturbingPresenceModifiers.add(modifier);
		} else {
			otherModifiers.put(modifier.getName(), modifier);
		}
	}

	public Set<V> getTacklezoneModifiers() {
		return tacklezoneModifiers;
	}

	public Set<V> getDisturbingPresenceModifiers() {
		return disturbingPresenceModifiers;
	}
}

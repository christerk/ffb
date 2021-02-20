package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.IRollModifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ModifierCollection<C extends ModifierContext, V extends IRollModifier<C>> implements IKeyedItem {
	private final Set<V> tacklezoneModifiers = new HashSet<>();
	private final Set<V> disturbingPresenceModifiers = new HashSet<>();
	private final Set<V> otherModifiers = new HashSet<>();
	private final Set<V> allModifiers = new HashSet<>();

	public Collection<V> getOtherModifiers() {
		return otherModifiers;
	};

	protected void add(V modifier) {
		if (modifier.isTacklezoneModifier()) {
			tacklezoneModifiers.add(modifier);
		} else if (modifier.isDisturbingPresenceModifier()) {
			disturbingPresenceModifiers.add(modifier);
		} else {
			otherModifiers.add(modifier);
		}
		allModifiers.add(modifier);
	}

	public Set<V> getTacklezoneModifiers() {
		return tacklezoneModifiers;
	}

	public Set<V> getDisturbingPresenceModifiers() {
		return disturbingPresenceModifiers;
	}

	public Set<V> getAllModifiers() {
		return allModifiers;
	}
}

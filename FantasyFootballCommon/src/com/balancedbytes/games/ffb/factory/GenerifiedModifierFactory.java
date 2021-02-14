package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.ModifierKey;
import com.balancedbytes.games.ffb.modifiers.ModifierRegistry;
import com.balancedbytes.games.ffb.util.Scanner;

public abstract class GenerifiedModifierFactory<
	K extends ModifierKey,
	V extends IRollModifier<K>,
	R extends ModifierRegistry<K, V>
	> implements IRollModifierFactory<V> {

	@Override
	public void initialize(Game game) {
		getScanner()
			.getSubclasses(game.getOptions()).stream().findFirst()
			.ifPresent(this::setRegistry);
	}

	protected abstract Scanner<R> getScanner();

	protected abstract R getRegistry();

	protected abstract void setRegistry(R registry);

	protected abstract V dummy();

	public V forKey(K key) {
		return getRegistry().get(key).orElse(dummy());
	}

}

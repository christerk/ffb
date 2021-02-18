package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IRollModifier;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModifierDictionary {
	private final Set<IRollModifier<?>> dictionary = new HashSet<>();

	public void add(IRollModifier<?> modifier) {
		dictionary.add(modifier);
	}

	public CatchModifier catchModifier(String name) {
		return dictionary.stream()
			.filter(modifier -> modifier instanceof CatchModifier && modifier.getName().equals(name))
			.map(modifier -> (CatchModifier) modifier)
			.findFirst()
			.orElse(null);
	}

	public InterceptionModifier interceptionModifier(String name) {
		return dictionary.stream()
			.filter(modifier -> modifier instanceof InterceptionModifier && modifier.getName().equals(name))
			.map(modifier -> (InterceptionModifier) modifier)
			.findFirst()
			.orElse(null);
	}
}

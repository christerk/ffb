package com.balancedbytes.games.ffb.factory.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.SpecialEffectInjuryModifier;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InjuryModifiers implements com.balancedbytes.games.ffb.factory.InjuryModifiers {

	private final Set<? extends InjuryModifier> injuryModifiers = new HashSet<InjuryModifier>() {{
		add(new SpecialEffectInjuryModifier("Bomb", 1, false, SpecialEffect.BOMB));
		add(new SpecialEffectInjuryModifier("Fireball", 1, false, SpecialEffect.FIREBALL));
		add(new SpecialEffectInjuryModifier("Lightning", 1, false, SpecialEffect.LIGHTNING));
	}};

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public Stream<? extends InjuryModifier> values() {
		return injuryModifiers.stream();
	}
}

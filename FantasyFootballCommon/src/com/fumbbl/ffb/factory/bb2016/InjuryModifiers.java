package com.fumbbl.ffb.factory.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.SpecialEffectInjuryModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifier;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InjuryModifiers implements com.fumbbl.ffb.factory.InjuryModifiers {

	private final Set<? extends InjuryModifier> injuryModifiers = new HashSet<InjuryModifier>() {{
		add(new StaticInjuryModifier("1 Niggling Injury", 1, true));
		add(new StaticInjuryModifier("2 Niggling Injuries", 2, true));
		add(new StaticInjuryModifier("3 Niggling Injuries", 3, true));
		add(new StaticInjuryModifier("4 Niggling Injuries", 4, true));
		add(new StaticInjuryModifier("5 Niggling Injuries", 5, true));
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

	@Override
	public Stream<? extends InjuryModifier> allValues() {
		return values();
	}

	@Override
	public void setUseAll(boolean useAll) {

	}
}

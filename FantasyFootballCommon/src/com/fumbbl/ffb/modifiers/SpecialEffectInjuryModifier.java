package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.SpecialEffect;

public class SpecialEffectInjuryModifier extends StaticInjuryModifier {

	private final SpecialEffect effect;

	public SpecialEffectInjuryModifier(String pName, int pModifier, boolean pNigglingInjuryModifier, SpecialEffect effect) {
		super(pName, pModifier, pNigglingInjuryModifier);
		this.effect = effect;
	}

	public SpecialEffect getEffect() {
		return effect;
	}
}

package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.SpecialEffect;

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

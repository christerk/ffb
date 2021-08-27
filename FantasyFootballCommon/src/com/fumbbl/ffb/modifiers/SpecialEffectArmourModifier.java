package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.SpecialEffect;

public class SpecialEffectArmourModifier extends StaticArmourModifier {

	private final SpecialEffect effect;

	public SpecialEffectArmourModifier(String pName, int pModifier, boolean pFoulAssistModifier, SpecialEffect effect) {
		super(pName, pModifier, pFoulAssistModifier);
		this.effect = effect;
	}

	public SpecialEffect getEffect() {
		return effect;
	}
}

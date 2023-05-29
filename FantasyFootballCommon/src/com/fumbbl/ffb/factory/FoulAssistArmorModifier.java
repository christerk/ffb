package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

public class FoulAssistArmorModifier extends StaticArmourModifier {

	public FoulAssistArmorModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
		super(pName, pModifier, pFoulAssistModifier);
	}

	@Override
	public boolean appliesToContext(ArmorModifierContext context) {
		return context.isFoul() && context.getFoulAssists() == getModifier(context.getAttacker());
	}
}

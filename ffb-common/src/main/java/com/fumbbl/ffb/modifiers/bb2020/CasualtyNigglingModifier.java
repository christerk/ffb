package com.fumbbl.ffb.modifiers.bb2020;

public class CasualtyNigglingModifier extends CasualtyModifier {

	public CasualtyNigglingModifier(String name, int modifier) {
		super(name, modifier);
	}

	@Override
	public String reportString() {
		return getName();
	}
}

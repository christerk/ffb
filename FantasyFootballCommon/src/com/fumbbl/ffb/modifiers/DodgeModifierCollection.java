package com.fumbbl.ffb.modifiers;

public abstract class DodgeModifierCollection extends ModifierCollection<DodgeContext, DodgeModifier>{

	public DodgeModifierCollection() {
		add(new DodgeModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new DodgeModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new DodgeModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new DodgeModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new DodgeModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new DodgeModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new DodgeModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new DodgeModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
	}
}

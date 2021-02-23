package com.balancedbytes.games.ffb.modifiers;

public class DodgeModifierCollection extends ModifierCollection<DodgeContext, DodgeModifier>{
	@Override
	public String getKey() {
		return "DodgeModifierCollection";
	}

	public DodgeModifierCollection() {
		add(new DodgeModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new DodgeModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new DodgeModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new DodgeModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new DodgeModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new DodgeModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new DodgeModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new DodgeModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
		add(new DodgeModifier("1 Prehensile Tail", 1, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("2 Prehensile Tails", 2, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("3 Prehensile Tails", 3, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("4 Prehensile Tails", 4, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("5 Prehensile Tails", 5, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("6 Prehensile Tails", 6, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("7 Prehensile Tails", 7, ModifierType.PREHENSILE_TAIL));
		add(new DodgeModifier("8 Prehensile Tails", 8, ModifierType.PREHENSILE_TAIL));
	}
}

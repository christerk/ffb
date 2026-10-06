package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class JumpModifierCollection extends com.fumbbl.ffb.modifiers.JumpModifierCollection {
	public JumpModifierCollection() {
		add(new JumpModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new JumpModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new JumpModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new JumpModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new JumpModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new JumpModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new JumpModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new JumpModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
		add(new JumpModifier("1 Prehensile Tail", "1 for being marked with Prehensile Tail", 1, 1, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("2 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 2, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("3 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 3, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("4 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 4, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("5 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 5, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("6 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 6, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("7 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 7, ModifierType.PREHENSILE_TAIL));
		add(new JumpModifier("8 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 8, ModifierType.PREHENSILE_TAIL));
	}
}

package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class DodgeModifierCollection extends com.fumbbl.ffb.modifiers.DodgeModifierCollection {
	public DodgeModifierCollection() {
		add(new DodgeModifier("1 Prehensile Tail", "1 for being marked with Prehensile Tail", 1, 1, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("2 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 2, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("3 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 3, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("4 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 4, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("5 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 5, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("6 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 6, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("7 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 7, ModifierType.PREHENSILE_TAIL, false));
		add(new DodgeModifier("8 Prehensile Tails", "1 for being marked with Prehensile Tail", 1, 8, ModifierType.PREHENSILE_TAIL, false));
	}
}

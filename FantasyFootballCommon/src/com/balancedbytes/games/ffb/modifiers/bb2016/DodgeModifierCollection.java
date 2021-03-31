package com.balancedbytes.games.ffb.modifiers.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.modifiers.DodgeModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class DodgeModifierCollection extends com.balancedbytes.games.ffb.modifiers.DodgeModifierCollection {
	public DodgeModifierCollection() {
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

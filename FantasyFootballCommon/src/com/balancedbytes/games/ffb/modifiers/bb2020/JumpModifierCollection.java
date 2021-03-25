package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class JumpModifierCollection extends com.balancedbytes.games.ffb.modifiers.JumpModifierCollection {
	public JumpModifierCollection() {
		add(new JumpModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new JumpModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new JumpModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new JumpModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new JumpModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new JumpModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new JumpModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new JumpModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
	}
}

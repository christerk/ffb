package com.balancedbytes.games.ffb.modifiers.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InterceptionModifierCollection extends com.balancedbytes.games.ffb.modifiers.InterceptionModifierCollection {

	public InterceptionModifierCollection() {
		super();
		add(new InterceptionModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
	}
}

package com.fumbbl.ffb.modifiers.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InterceptionModifierCollection extends com.fumbbl.ffb.modifiers.InterceptionModifierCollection {

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

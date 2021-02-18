package com.balancedbytes.games.ffb.modifiers.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;

@RulesCollection(RulesCollection.Rules.BB2016)
public class InterceptionModifierCollection extends com.balancedbytes.games.ffb.modifiers.InterceptionModifierCollection {

	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		super.postConstruct(dictionary);
		add(new InterceptionModifier("1 Tacklezone", 1, true, false, dictionary));
		add(new InterceptionModifier("2 Tacklezones", 2, true, false, dictionary));
		add(new InterceptionModifier("3 Tacklezones", 3, true, false, dictionary));
		add(new InterceptionModifier("4 Tacklezones", 4, true, false, dictionary));
		add(new InterceptionModifier("5 Tacklezones", 5, true, false, dictionary));
		add(new InterceptionModifier("6 Tacklezones", 6, true, false, dictionary));
		add(new InterceptionModifier("7 Tacklezones", 7, true, false, dictionary));
		add(new InterceptionModifier("8 Tacklezones", 8, true, false, dictionary));
	}
}

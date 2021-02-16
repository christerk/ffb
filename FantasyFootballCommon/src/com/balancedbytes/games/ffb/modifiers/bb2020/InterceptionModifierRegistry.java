package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InterceptionModifierRegistry extends com.balancedbytes.games.ffb.modifiers.InterceptionModifierRegistry {

	public InterceptionModifierRegistry() {
		add(new InterceptionModifier("Extra Arms", -1, false, false));
		add(new InterceptionModifier("Very Long Legs", -1, false,
			false));
		add(new InterceptionModifier("Pouring Rain", 1, false, false));
		add(new InterceptionModifier("1 Tacklezone", "1 for 1+ Tacklezones", 1, 1, true, false));
		add(new InterceptionModifier("1 Tacklezone", 1, true, false));
		add(new InterceptionModifier("2 Tacklezones", 2, true, false));
		add(new InterceptionModifier("3 Tacklezones", 3, true, false));
		add(new InterceptionModifier("4 Tacklezones", 4, true, false));
		add(new InterceptionModifier("5 Tacklezones", 5, true, false));
		add(new InterceptionModifier("6 Tacklezones", 6, true, false));
		add(new InterceptionModifier("7 Tacklezones", 7, true, false));
		add(new InterceptionModifier("8 Tacklezones", 8, true, false));
		add(new InterceptionModifier("1 Disturbing Presence", 1,
			false, true));
		add(new InterceptionModifier("2 Disturbing Presences",
			2, false, true));
		add(new InterceptionModifier("3 Disturbing Presences",
			3, false, true));
		add(new InterceptionModifier("4 Disturbing Presences",
			4, false, true));
		add(new InterceptionModifier("5 Disturbing Presences",
			5, false, true));
		add(new InterceptionModifier("6 Disturbing Presences",
			6, false, true));
		add(new InterceptionModifier("7 Disturbing Presences",
			7, false, true));
		add(new InterceptionModifier("8 Disturbing Presences",
			8, false, true));
		add(new InterceptionModifier("9 Disturbing Presences",
			9, false, true));
		add(new InterceptionModifier("10 Disturbing Presences",
			10, false, true));
		add(new InterceptionModifier("11 Disturbing Presences",
			11, false, true));
		add(new InterceptionModifier("Fawndough's Headband", -1,
			false, false));
		add(new InterceptionModifier(
			"Magic Gloves of Jark Longarm", -1, false, false));
		add(new InterceptionModifier("Accurate Pass", 3, false, false));
		add(new InterceptionModifier("Inaccurate Pass", 2, false, false));
		add(new InterceptionModifier("Wildly Inaccurate Pass", 1, false, false));
	}
}

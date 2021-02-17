package com.balancedbytes.games.ffb.modifiers;

public abstract class InterceptionModifierCollection extends ModifierCollection<InterceptionContext, InterceptionModifier> {

	public InterceptionModifierCollection() {
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

	}

	@Override
	public String getKey() {
		return "InterceptionModifierRegistry";
	}
}

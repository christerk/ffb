package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.ModifierDictionary;

public abstract class InterceptionModifierCollection extends ModifierCollection<InterceptionContext, InterceptionModifier> {
	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		add(new InterceptionModifier("1 Disturbing Presence", 1, false, true, dictionary));
		add(new InterceptionModifier("2 Disturbing Presences", 2, false, true, dictionary));
		add(new InterceptionModifier("3 Disturbing Presences", 3, false, true, dictionary));
		add(new InterceptionModifier("4 Disturbing Presences", 4, false, true, dictionary));
		add(new InterceptionModifier("5 Disturbing Presences", 5, false, true, dictionary));
		add(new InterceptionModifier("6 Disturbing Presences", 6, false, true, dictionary));
		add(new InterceptionModifier("7 Disturbing Presences", 7, false, true, dictionary));
		add(new InterceptionModifier("8 Disturbing Presences", 8, false, true, dictionary));
		add(new InterceptionModifier("9 Disturbing Presences", 9, false, true, dictionary));
		add(new InterceptionModifier("10 Disturbing Presences", 10, false, true, dictionary));
		add(new InterceptionModifier("11 Disturbing Presences", 11, false, true, dictionary));
		add(new InterceptionModifier("Pouring Rain", 1, false, false, dictionary) {
			@Override
			public boolean appliesToContext(InterceptionContext context) {
				return super.appliesToContext(context) && context.getGame().getFieldModel().getWeather().equals(Weather.POURING_RAIN);
			}
		});
	}

	@Override
	public String getKey() {
		return "InterceptionModifierRegistry";
	}
}

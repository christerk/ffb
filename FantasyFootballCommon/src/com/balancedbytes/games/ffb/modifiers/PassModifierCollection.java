package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;

public abstract class PassModifierCollection extends ModifierCollection<PassContext, PassModifier> {
	@Override
	public String getKey() {
		return "PassModifierCollection";
	}

	public PassModifierCollection() {
		add(new PassModifier("Very Sunny", 1, false, false) {
			@Override
			public boolean appliesToContext(PassContext context) {
				return super.appliesToContext(context) && context.getGame().getFieldModel().getWeather().equals(Weather.VERY_SUNNY);
			}
		});
		add(new PassModifier("1 Tacklezone", 1, true, false));
		add(new PassModifier("2 Tacklezones", 2, true, false));
		add(new PassModifier("3 Tacklezones", 3, true, false));
		add(new PassModifier("4 Tacklezones", 4, true, false));
		add(new PassModifier("5 Tacklezones", 5, true, false));
		add(new PassModifier("6 Tacklezones", 6, true, false));
		add(new PassModifier("7 Tacklezones", 7, true, false));
		add(new PassModifier("8 Tacklezones", 8, true, false));
		add(new PassModifier("1 Disturbing Presence", 1, false, true));
		add(new PassModifier("2 Disturbing Presences", 2, false, true));
		add(new PassModifier("3 Disturbing Presences", 3, false, true));
		add(new PassModifier("4 Disturbing Presences", 4, false, true));
		add(new PassModifier("5 Disturbing Presences", 5, false, true));
		add(new PassModifier("6 Disturbing Presences", 6, false, true));
		add(new PassModifier("7 Disturbing Presences", 7, false, true));
		add(new PassModifier("8 Disturbing Presences", 8, false, true));
		add(new PassModifier("9 Disturbing Presences", 9, false, true));
		add(new PassModifier("10 Disturbing Presences", 10, false,
			true));
		add(new PassModifier("11 Disturbing Presences", 11, false,
			true));
	}
}

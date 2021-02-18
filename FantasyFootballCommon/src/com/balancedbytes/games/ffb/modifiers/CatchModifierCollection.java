package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.ModifierDictionary;

public abstract class CatchModifierCollection extends ModifierCollection<CatchContext, CatchModifier> {

	@Override
	public String getKey() {
		return "CatchModifierRegistry";
	}

	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		add(new CatchModifier("1 Tacklezone", 1, true, false, dictionary));
		add(new CatchModifier("2 Tacklezones", 2, true, false, dictionary));
		add(new CatchModifier("3 Tacklezones", 3, true, false, dictionary));
		add(new CatchModifier("4 Tacklezones", 4, true, false, dictionary));
		add(new CatchModifier("5 Tacklezones", 5, true, false, dictionary));
		add(new CatchModifier("6 Tacklezones", 6, true, false, dictionary));
		add(new CatchModifier("7 Tacklezones", 7, true, false, dictionary));
		add(new CatchModifier("8 Tacklezones", 8, true, false, dictionary));
		add(new CatchModifier("1 Disturbing Presence", 1, false, true, dictionary));
		add(new CatchModifier("2 Disturbing Presences", 2, false, true, dictionary));
		add(new CatchModifier("3 Disturbing Presences", 3, false, true, dictionary));
		add(new CatchModifier("4 Disturbing Presences", 4, false, true, dictionary));
		add(new CatchModifier("5 Disturbing Presences", 5, false, true, dictionary));
		add(new CatchModifier("6 Disturbing Presences", 6, false, true, dictionary));
		add(new CatchModifier("7 Disturbing Presences", 7, false, true, dictionary));
		add(new CatchModifier("8 Disturbing Presences", 8, false, true, dictionary));
		add(new CatchModifier("9 Disturbing Presences", 9, false, true, dictionary));
		add(new CatchModifier("10 Disturbing Presences", 10, false, true, dictionary));
		add(new CatchModifier("11 Disturbing Presences", 11, false, true, dictionary));
		add(new CatchModifier("Pouring Rain", 1, false, false, dictionary) {
			@Override
			public boolean appliesToContext(CatchContext context) {
				return super.appliesToContext(context) && context.getGame().getFieldModel().getWeather().equals(Weather.POURING_RAIN);
			}
		});
	}
}

package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Skill;

public abstract class CatchModifierCollection extends ModifierCollection<CatchContext, CatchModifier> {

	public CatchModifierCollection() {
		add(new CatchModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new CatchModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new CatchModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new CatchModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new CatchModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new CatchModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new CatchModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new CatchModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
		add(new CatchModifier("1 Disturbing Presence", 1, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("2 Disturbing Presences", 2, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("3 Disturbing Presences", 3, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("4 Disturbing Presences", 4, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("5 Disturbing Presences", 5, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("6 Disturbing Presences", 6, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("7 Disturbing Presences", 7, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("8 Disturbing Presences", 8, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("9 Disturbing Presences", 9, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("10 Disturbing Presences", 10, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("11 Disturbing Presences", 11, ModifierType.DISTURBING_PRESENCE));
		add(new CatchModifier("Pouring Rain", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {
				return super.appliesToContext(skill, context) && context.getGame().getFieldModel().getWeather().equals(Weather.POURING_RAIN);
			}
		});
	}
}

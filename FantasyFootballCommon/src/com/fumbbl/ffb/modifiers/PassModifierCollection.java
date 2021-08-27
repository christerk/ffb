package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.skill.Skill;

public class PassModifierCollection extends ModifierCollection<PassContext, PassModifier> {

	public PassModifierCollection() {
		add(new PassModifier("Very Sunny", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return super.appliesToContext(skill, context) && context.getGame().getFieldModel().getWeather().equals(Weather.VERY_SUNNY);
			}
		});
		add(new PassModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new PassModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new PassModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new PassModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new PassModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new PassModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new PassModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new PassModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
		add(new PassModifier("1 Disturbing Presence", 1, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("2 Disturbing Presences", 2, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("3 Disturbing Presences", 3, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("4 Disturbing Presences", 4, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("5 Disturbing Presences", 5, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("6 Disturbing Presences", 6, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("7 Disturbing Presences", 7, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("8 Disturbing Presences", 8, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("9 Disturbing Presences", 9, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("10 Disturbing Presences", 10, ModifierType.DISTURBING_PRESENCE));
		add(new PassModifier("11 Disturbing Presences", 11, ModifierType.DISTURBING_PRESENCE));
	}
}

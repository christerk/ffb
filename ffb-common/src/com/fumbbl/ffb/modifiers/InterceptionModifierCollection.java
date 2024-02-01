package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.skill.Skill;

public abstract class InterceptionModifierCollection extends ModifierCollection<InterceptionContext, InterceptionModifier> {

	public InterceptionModifierCollection() {
		super();
		add(new InterceptionModifier("1 Disturbing Presence", 1, ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("2 Disturbing Presences", 2, ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("3 Disturbing Presences", 3, ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("4 Disturbing Presences", 4,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("5 Disturbing Presences", 5,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("6 Disturbing Presences", 6,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("7 Disturbing Presences", 7,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("8 Disturbing Presences", 8,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("9 Disturbing Presences", 9,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("10 Disturbing Presences", 10,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("11 Disturbing Presences", 11,ModifierType.DISTURBING_PRESENCE));
		add(new InterceptionModifier("Pouring Rain", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, InterceptionContext context) {
				return super.appliesToContext(skill, context) && context.getGame().getFieldModel().getWeather().equals(Weather.POURING_RAIN);
			}
		});
	}
}

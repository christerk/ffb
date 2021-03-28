package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

public class GoForItModifierCollection extends ModifierCollection<GoForItContext, GoForItModifier> {
	public GoForItModifierCollection() {
		add(new GoForItModifier("Blizzard", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return !context.getGame().isActive(NamedProperties.setGfiRollToFive) && context.getGame().getFieldModel().getWeather() == Weather.BLIZZARD;
			}
		});
	}
}

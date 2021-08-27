package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

public class GoForItModifierCollection extends ModifierCollection<GoForItContext, GoForItModifier> {
	public GoForItModifierCollection() {
		add(new GoForItModifier("Blizzard", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return !context.getGame().isActive(NamedProperties.setGfiRollToFive) && context.getGame().getFieldModel().getWeather() == Weather.BLIZZARD;
			}
		});

		add(new GoForItModifier("Moles under the Pitch (Home)", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return context.getTeamsWithMolesUnderThePitch().contains(context.getGame().getTeamHome().getId());
			}
		});

		add(new GoForItModifier("Moles under the Pitch (Away)", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return context.getTeamsWithMolesUnderThePitch().contains(context.getGame().getTeamAway().getId());
			}
		});

	}
}

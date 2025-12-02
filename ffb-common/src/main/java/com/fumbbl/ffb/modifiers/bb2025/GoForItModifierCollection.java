package com.fumbbl.ffb.modifiers.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.GoForItContext;
import com.fumbbl.ffb.modifiers.GoForItModifier;

@RulesCollection(RulesCollection.Rules.BB2025)
public class GoForItModifierCollection extends com.fumbbl.ffb.modifiers.GoForItModifierCollection {
	public GoForItModifierCollection() {
		add(new GoForItModifier("Blizzard", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return !context.getGame().isActive(NamedProperties.setGfiRollToFive) && context.getGame().getFieldModel().getWeather() == Weather.BLIZZARD;
			}
		});

		add(new GoForItModifier("Moles under the Pitch", 1) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return context.getTeamsWithMolesUnderThePitch().stream().map(id -> context.getGame().getTeamById(id))
					.anyMatch(team -> !team.hasPlayer(context.getPlayer()));
			}
		});
	}
}

package com.fumbbl.ffb.modifiers.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PassModifierCollection extends com.fumbbl.ffb.modifiers.PassModifierCollection {

	public PassModifierCollection() {
		add(new PassModifier("Blizzard", 0, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return super.appliesToContext(skill, context) && context.getGame().getFieldModel().getWeather().equals(Weather.BLIZZARD);
			}
		});
	}
}

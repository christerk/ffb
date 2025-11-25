package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PickupModifier;

/**
 * This player ignores all negative modifiers when attempting to pick up the ball.
 */
@RulesCollection(Rules.BB2025)
public class BigHand extends Skill {

	public BigHand() {
		super("Big Hand", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.ignoreTacklezonesWhenPickingUp);
		registerProperty(NamedProperties.ignoreWeatherWhenPickingUp);
		registerModifier(new PickupModifier("Big Hand", "0 ignoring all tackle zones due to Big Hand", 0, ModifierType.REGULAR) {
			@Override
			public boolean isModifierIncluded() {
				return true;
			}
		});
	}

}

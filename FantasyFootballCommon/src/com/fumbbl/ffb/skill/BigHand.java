package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PickupModifier;

/**
 * One of the player's hands has grown monstrously large, yet remained
 * completely functional. The player ignores modifier(s) for enemy tackle zones
 * or Pouring Rain weather when he attempts to pick up the ball.
 */
@RulesCollection(Rules.COMMON)
public class BigHand extends Skill {

	public BigHand() {
		super("Big Hand", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.ignoreTacklezonesWhenPickingUp);
		registerProperty(NamedProperties.ignoreWeatherWhenPickingUp);
		registerModifier(new PickupModifier("Big Hand", "0 ignoring all tackle zones and weather effects due to Big Hand", 0, ModifierType.REGULAR) {
			@Override
			public boolean isModifierIncluded() {
				return true;
			}
		});
	}

}

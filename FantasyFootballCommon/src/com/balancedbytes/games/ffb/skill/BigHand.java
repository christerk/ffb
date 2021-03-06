package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.PickupModifier;

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

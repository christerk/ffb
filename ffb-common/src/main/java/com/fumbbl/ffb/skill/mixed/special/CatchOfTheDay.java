package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per half, if Rodney is Standing and begins his activation within 3 squares of a ball which is on the ground
 * he may roll a D6. On a 1 or 2, nothing happens. On a 3+, Rodney immediately gains possession of the ball.
 */

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class CatchOfTheDay extends Skill {
	public CatchOfTheDay() {
		super("Catch of the Day", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGetBallOnGround);
	}
}

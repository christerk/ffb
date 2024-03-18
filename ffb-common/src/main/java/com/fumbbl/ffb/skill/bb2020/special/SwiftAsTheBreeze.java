package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, Jordell can choose to pass a single Dodge, Leap or Rush test on a 2+, regardless of any modifiers.
 */

@RulesCollection(RulesCollection.Rules.BB2020)
public class SwiftAsTheBreeze extends Skill {
	public SwiftAsTheBreeze() {
		super("Swift As The Breeze", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMakeUnmodifiedDodgeJumpOrRush);
	}
}

package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Team-mates within 3 squares of a Standing player with this Trait do not roll
 * for their Squig Rage Trait at the start of their activation and may activate
 * as normal.
 */
@RulesCollection(Rules.BB2025)
public class SquigRestraint extends Skill {

	public SquigRestraint() {
		super("Squig Restraint", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventsRageRollOfNearbyTeamMates);
	}

}

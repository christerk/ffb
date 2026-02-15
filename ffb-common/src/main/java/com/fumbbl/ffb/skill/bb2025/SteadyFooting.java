package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Whenever this player would be Knocked Down or Fall Over, roll a D6. On a 6, this player does not get Knocked Down or
 * Fall Over. If this happens during their activation, they may continue their activation as normal and no Turnover will
 * be caused.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class SteadyFooting extends Skill {

	public SteadyFooting() {
		super("Steady Footing", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAvoidFallingDown);
		registerConflictingProperty(NamedProperties.movesRandomly);
	}
}

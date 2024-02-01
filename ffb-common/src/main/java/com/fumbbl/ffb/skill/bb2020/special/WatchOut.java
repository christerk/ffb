package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * The first time each half that Withergrasp is the target of an opposition player's
 * Block action, he counts as having the Dodge skill
 */

@RulesCollection(Rules.BB2020)
public class WatchOut extends Skill {
	public WatchOut() {
		super("Watch Out!", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.ignoresDefenderStumblesResultForFirstBlock);
	}
}

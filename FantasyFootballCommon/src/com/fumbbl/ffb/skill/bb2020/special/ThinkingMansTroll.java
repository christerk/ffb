package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per half, Ripper may re-roll one die that was rolled either as a single dice roll,
 * as port of a multiple dice roll or as port of a dice pool (this cannot be a dice that was rolled
 * as part of an Armour, Injury or Casualty roll)
 */

@RulesCollection(Rules.BB2020)
public class ThinkingMansTroll extends Skill {
	public ThinkingMansTroll() {
		super("Thinking Man's Troll", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleDieOncePerGame);
	}
}

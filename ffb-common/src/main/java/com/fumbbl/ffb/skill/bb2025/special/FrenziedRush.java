package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.skill.mixed.Frenzy;

import java.util.Collections;

/**
 * Once per half, when Glart declares a Blitz Action he may gain the Frenzy Skill 
 * until the end of his activation. Glatt may not use the Grab Skill during a 
 * Turn in which he uses this special rule.
 */

@RulesCollection(Rules.BB2025)
public class FrenziedRush extends Skill {
	public FrenziedRush() {
		super("Frenzied Rush", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGainFrenzyForBlitz);
		setEnhancements(new TemporaryEnhancements()
			.withSkills(Collections.singleton(new SkillClassWithValue(Frenzy.class))));
	}
}

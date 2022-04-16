package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.skill.bb2020.HypnoticGaze;

import java.util.Collections;

/**
 * Once per game, when Zolcath is activated, he may gain the Hypnotic Gaze trait.
 * You must declare this special rule is being used when Zolcath is activated
 */

@RulesCollection(Rules.BB2020)
public class ExcuseMeAreYouAZoat extends Skill {
	public ExcuseMeAreYouAZoat() {
		super("\"Excuse Me, Are You a Zoat?\"", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGainGaze);
		setEnhancements(new TemporaryEnhancements()
			.withSkills(Collections.singleton(new SkillClassWithValue(HypnoticGaze.class))));
	}

}

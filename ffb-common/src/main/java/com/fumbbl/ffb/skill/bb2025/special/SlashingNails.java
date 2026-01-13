package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.skill.mixed.Claws;

import java.util.Collections;

/**
 * Once per half, when Roxanna declares a Blitz Action, she gains the Claws Skill 
 * until the end of her activation
 */

@RulesCollection(Rules.BB2025)
public class SlashingNails extends Skill {
	public SlashingNails() {
		super("Slashing Nails", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGainClawsForBlitz);
		setEnhancements(new TemporaryEnhancements()
			.withSkills(Collections.singleton(new SkillClassWithValue(Claws.class))));
	}
}
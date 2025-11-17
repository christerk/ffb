package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

@RulesCollection(RulesCollection.Rules.BB2025)
public class PutridRegurgitation extends Skill {
	public PutridRegurgitation() {
		super("Putrid Regurgitation", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canUseVomitAfterBlock);
		registerProperty(NamedProperties.providesBlockAlternative);
		registerProperty(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
	}
}

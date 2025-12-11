package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BeerBarrelBash extends Skill {
	public BeerBarrelBash() {
		super("Beer Barrel Bash!", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_DRIVE);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canThrowKeg);
	}
}

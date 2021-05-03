package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ProjectileVomit extends Skill {
	public ProjectileVomit() {
		super("Projectile Vomit", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.providesBlockAlternative);
		registerProperty(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
	}
}

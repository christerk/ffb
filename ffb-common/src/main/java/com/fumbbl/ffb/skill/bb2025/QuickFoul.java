package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2025)
public class QuickFoul extends Skill {

	public QuickFoul() {
		super("Quick Foul", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMoveAfterFoul);
	}
}
package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Defensive extends Skill {
	public Defensive() {
		super("Defensive", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.assistsBlocksInTacklezones));
		registerProperty(new CancelSkillProperty(NamedProperties.assistsFoulsInTacklezones));
	}
}

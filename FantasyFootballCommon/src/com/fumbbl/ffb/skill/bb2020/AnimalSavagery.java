package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class AnimalSavagery extends Skill {

	public AnimalSavagery() {
		super("Animal Savagery", SkillCategory.TRAIT, true);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.enableStandUpAndEndBlitzAction);
		registerProperty(NamedProperties.needsToRollForActionBlockingIsEasier);
	}

	@Override
	public String getConfusionMessage() {
		return "tries to lash out against a team mate";
	}
}

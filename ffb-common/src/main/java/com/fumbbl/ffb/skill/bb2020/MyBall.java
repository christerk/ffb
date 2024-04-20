package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class MyBall extends Skill {

	public MyBall() {
		super("My Ball", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventRegularHandOverAction);
		registerProperty(NamedProperties.preventRegularPassAction);
		registerProperty(new CancelSkillProperty(NamedProperties.canDropBall));
	}

}

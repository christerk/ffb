package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2020)
public class OnTheBall extends Skill {

	public OnTheBall() {
		super("On The Ball", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMoveDuringKickOffScatter);
		registerProperty(NamedProperties.canMoveWhenOpponentPasses);

	}

}

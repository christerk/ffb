package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
public class KeenPlayer extends Skill {
	public KeenPlayer() {
		super("Keen Player", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canJoinTeamIfLessThanEleven);
		registerProperty(NamedProperties.getsSentOffAtEndOfDrive);
	}
}

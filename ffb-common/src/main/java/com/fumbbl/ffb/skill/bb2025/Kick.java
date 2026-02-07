package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2025)
public class Kick extends Skill {

	public Kick() {
		super("Kick", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canReduceKickDistance);
		registerRerollSource(ReRolledActions.PUNT_DIRECTION, ReRollSources.KICK);
		registerRerollSource(ReRolledActions.PUNT_DISTANCE, ReRollSources.KICK);
	}

}

package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

@RulesCollection(RulesCollection.Rules.BB2025)
public class WhirlingDervish extends Skill {

	public WhirlingDervish() {
		super("Whirling Dervish", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_TURN);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.DIRECTION, ReRollSources.WHIRLING_DERVISH);
	}
}

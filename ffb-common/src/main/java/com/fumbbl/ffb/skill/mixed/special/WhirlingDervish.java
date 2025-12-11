package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class WhirlingDervish extends Skill {

	public WhirlingDervish() {
		super("Whirling Dervish", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.DIRECTION, ReRollSources.WHIRLING_DERVISH);
	}
}

package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Brawler extends Skill {
	public Brawler() {
		super("Brawler", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleBothDown);
		registerRerollSource(ReRolledActions.SINGLE_BOTH_DOWN, ReRollSources.BRAWLER);
	}
}

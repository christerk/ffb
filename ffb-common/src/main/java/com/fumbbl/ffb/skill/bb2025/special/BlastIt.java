package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2025)
public class BlastIt extends Skill {

	public BlastIt() {
		super("Blast It!", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canReRollHmpScatter);
		registerProperty(NamedProperties.grantsCatchBonusToReceiver);
	}
}

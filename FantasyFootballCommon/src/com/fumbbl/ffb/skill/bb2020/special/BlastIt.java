package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class BlastIt extends Skill {

	public BlastIt() {
		super("Blast It!", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canReRollHmpScatter);
		registerProperty(NamedProperties.grantsCatchBonusToReceiver);
	}

	@Override
	public String[] getSkillUseDescription() {
		return new String[]{
			"This only re-rolls this scatter.",
			"You will be prompted again for remaining scatters."};
	}
}

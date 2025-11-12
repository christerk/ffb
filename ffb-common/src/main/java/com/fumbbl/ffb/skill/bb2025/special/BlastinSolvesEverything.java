package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

// /skill add "Blastin'_Solves_Everything" 5
@RulesCollection(RulesCollection.Rules.BB2025)
public class BlastinSolvesEverything extends Skill {

	public BlastinSolvesEverything() {
		super("\"Blastin' Solves Everything\"", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canBlastRemotePlayer);
	}
}

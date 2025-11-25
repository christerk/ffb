package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BreatheFire extends Skill {
	public BreatheFire() {
		super("Breathe Fire", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover);
		registerProperty(NamedProperties.providesBlockAlternative);
	}
}

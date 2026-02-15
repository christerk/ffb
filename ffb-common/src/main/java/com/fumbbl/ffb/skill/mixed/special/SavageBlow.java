package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SavageBlow extends Skill {
	public SavageBlow() {
		super("Savage Blow", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canReRollAnyNumberOfBlockDice);
		registerRerollSource(ReRolledActions.MULTI_BLOCK_DICE, ReRollSources.SAVAGE_BLOW);
	}
}

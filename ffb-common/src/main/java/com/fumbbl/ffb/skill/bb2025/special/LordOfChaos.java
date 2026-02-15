package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when Borak performs a Block Action he may reroll a single Block Dice. 
 */

@RulesCollection(Rules.BB2025)
public class LordOfChaos extends Skill {
	public LordOfChaos() {
		super("Lord of Chaos", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleBlockDieOncePerPeriod);
		registerRerollSource(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.LORD_OF_CHAOS);
	}
}

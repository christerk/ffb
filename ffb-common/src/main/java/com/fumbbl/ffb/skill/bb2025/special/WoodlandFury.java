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
 * Once per game, when Willow performs a Block Action that would result in her being Knocked Down,
 * she can choose to re-roll a single Block Dice.
 */

@RulesCollection(Rules.BB2025)
public class WoodlandFury extends Skill {
	public WoodlandFury() {
		super("Woodland Fury", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRerollSingleBlockDieWhenWouldBeKnockedDown);
		registerRerollSource(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.WOODLAND_FURY);
	}
}

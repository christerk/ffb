package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.StatBasedRollModifierFactory;

/**
 * Once per game, after making a Passing Ability test to perform a Pass action, Skrull may choose to modify the dice roll by adding his Strength characteristic to it
 */

@RulesCollection(Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StrongPassingGame extends Skill {
	public StrongPassingGame() {
		super("Strong Passing Game", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAddStrengthToPass);
		setStatBasedRollModifierFactory(new StatBasedRollModifierFactory(getName(), PlayerStatKey.ST));
	}
}

package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player's appearance is so horrible that any opposing player that wants to
 * block the player (or use a special attack that takes the place of a block)
 * must first roll a D6 and score 2 or more. If the opposing player rolls a 1 he
 * is too revolted to make the block and it is wasted (though the opposing team
 * does not suffer a turnover).
 */
@RulesCollection(Rules.COMMON)
public class FoulAppearance extends Skill {

	public FoulAppearance() {
		super("Foul Appearance", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.forceRollBeforeBeingBlocked);
	}
}

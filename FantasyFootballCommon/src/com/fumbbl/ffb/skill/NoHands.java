package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player is unable to pick up, intercept or carry the ball and will fail
 * any catch roll automatically, either because he literally has no hands or
 * because his hands are full. If he attempts to pick up the ball then it will
 * bounce, and will causes a turnover if it is his team's turn.
 */
@RulesCollection(Rules.COMMON)
public class NoHands extends Skill {

	public NoHands() {
		super("No Hands", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventCatch);
		registerProperty(NamedProperties.preventHoldBall);
		registerProperty(NamedProperties.preventRegularPassAction);
		registerProperty(NamedProperties.preventRegularHandOverAction);
	}

}

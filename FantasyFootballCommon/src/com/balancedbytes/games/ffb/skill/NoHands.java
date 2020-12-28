package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * The player is unable to pick up, intercept or carry the ball and will fail
 * any catch roll automatically, either because he literally has no hands or
 * because his hands are full. If he attempts to pick up the ball then it will
 * bounce, and will causes a turnover if it is his team's turn.
 */
public class NoHands extends Skill {

	public NoHands() {
		super("No Hands", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventCatch);
		registerProperty(NamedProperties.preventHoldBall);
	}

}

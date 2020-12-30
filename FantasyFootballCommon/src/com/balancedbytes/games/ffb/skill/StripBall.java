package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * When a player with this skill blocks an opponent with the ball, applying a
 * "Pushed" or "Defender Stumbles" result will cause the opposing player to drop
 * the ball in the square that they are pushed to, even if the opposing player
 * is not Knocked Down.
 */
@RulesCollection(Rules.COMMON)
public class StripBall extends Skill {

	public StripBall() {
		super("Strip Ball", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.forceOpponentToDropBallOnPushback);
	}

}

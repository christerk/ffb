package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

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

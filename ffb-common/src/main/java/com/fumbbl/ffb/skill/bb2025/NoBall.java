package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this Trait may never have possession of the ball. 
 * If this player would be required to attempt to Catch or Pick-up the Ball, 
 * they will automatically fail to do so as if they had rolled a natural 1. 
 * A player with this Trait may not attempt to Intercept a Pass.
 */
@RulesCollection(Rules.BB2025)
public class NoBall extends Skill {

	public NoBall() {
		super("No Ball", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventCatch);
		registerProperty(NamedProperties.preventHoldBall);
		registerProperty(NamedProperties.preventRegularPassAction);
		registerProperty(NamedProperties.preventRegularHandOverAction);
		registerProperty(NamedProperties.preventSecureTheBallAction);
		registerProperty(NamedProperties.preventPuntAction);
	}

}
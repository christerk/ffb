package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player can be thrown by a team-mate with the Throw Team-mate Trait, 
 * even if this player is Prone.
 */
@RulesCollection(Rules.BB2025)
public class RightStuff extends Skill {

	public RightStuff() {
		super("Right Stuff", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canBeThrown);
		registerProperty(NamedProperties.ignoreTackleWhenBlocked);
	}

}

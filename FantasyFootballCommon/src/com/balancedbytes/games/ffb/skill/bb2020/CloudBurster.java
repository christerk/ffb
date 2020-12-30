package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * When this player performs a Long Pass action or a Long Bomb Pass action,
 * you may apply an additional +1 modifier to the Passing Ability test.
 */
@RulesCollection(Rules.BB2020)
public class CloudBurster extends Skill {

	public CloudBurster() {
		super("Cloud Burster", SkillCategory.PASSING);
		
		registerProperty(NamedProperties.canForceInterceptionReroll);
	}
}

package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When this player performs a Long Pass action or a Long Bomb Pass
 * action, you may choose to make the opposing coach re-roll a
 * successful attempt to interfere with the pass.
 */
@RulesCollection(Rules.BB2020)
public class CloudBurster extends Skill {

	public CloudBurster() {
		super("Cloud Burster", SkillCategory.PASSING);
		
		registerProperty(NamedProperties.canForceInterceptionRerollOfLongPasses);
	}
}

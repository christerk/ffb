package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When this player performs a Pass Action, opposition player may 
 * not attempt to Intercept the ball
 */
@RulesCollection(Rules.BB2025)
public class CloudBurster extends Skill {

	public CloudBurster() {
		super("Cloud Burster", SkillCategory.PASSING);
		
		registerProperty(NamedProperties.passesAreNotIntercepted);
	}
}

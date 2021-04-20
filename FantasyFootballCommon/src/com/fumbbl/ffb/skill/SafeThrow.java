package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player is an expert at throwing the ball in a way so as to make it even
 * more difficult for any opponent to intercept it. If a pass made by this
 * player is ever intercepted then the Safe Throw player may make an unmodified
 * Agility roll. If this is successful then the interception is cancelled out
 * and the passing sequence continues as normal.
 */
@RulesCollection(Rules.COMMON)
public class SafeThrow extends Skill {

	public SafeThrow() {
		super("Safe Throw", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canCancelInterceptions);
		registerProperty(NamedProperties.dontDropFumbles);
	}

}

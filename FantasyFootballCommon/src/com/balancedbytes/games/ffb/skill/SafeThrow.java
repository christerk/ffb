package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

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

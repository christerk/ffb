package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player keeps a wapstone-tainted dagger hidden in their kit, and is an
 * expert at keeping it out of the referee's sight! If this player inflicts a
 * casualty during a block, and the result of the Casualty roll is 11-38 (Badly
 * Hurt) after any re-rolls, roll a D6. On a result of 4 or more, the opposing
 * player must miss their next game. If you are not playing a league, a Weeping
 * Dagger has no effect on the game.
 */
@RulesCollection(Rules.BB2016)
public class WeepingDagger extends Skill {

	public WeepingDagger() {
		super("Weeping Dagger", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.appliesPoisonOnBadlyHurt);
	}

}

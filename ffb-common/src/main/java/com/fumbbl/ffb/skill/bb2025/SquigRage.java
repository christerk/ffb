package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Whenever this player is activated, before declaring their action, they must
 * roll a D6. On a 3+ the player may act as normal. On a 1-2, the player must
 * declare a Block action targeting an opposition player they are Marking if
 * able (using Jump Up if applicable), even if they have Ball &amp; Chain. If no
 * such Block can be declared but the player is adjacent to at least one
 * team-mate, they may choose an adjacent team-mate to be Knocked Down (no
 * turnover unless that team-mate held the ball) and may then continue their
 * activation as normal. Otherwise this player becomes Distracted.
 */
@RulesCollection(Rules.BB2025)
public class SquigRage extends Skill {

	public SquigRage() {
		super("Squig Rage", SkillCategory.TRAIT, true);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.requiresRageRollBeforeDeclaringAction);
	}

	@Override
	public String getConfusionMessage() {
		return "flies into a rage";
	}

}

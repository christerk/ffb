package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill is able to quickly get back into the game. If the
 * player declares any Action other than a Block Action he may stand up for free
 * without paying the three squares of movement. The player may also declare a
 * Block Action while Prone which requires an Agility roll with a +2 modifier to
 * see if he can complete the Action. A successful roll means the player can
 * stand up for free and block an adjacent opponent. A failed roll means the
 * Block Action is wasted and the player may not stand up.
 */
@RulesCollection(Rules.COMMON)
public class JumpUp extends Skill {

	public JumpUp() {
		super("Jump Up", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canStandUpForFree);
	}

}

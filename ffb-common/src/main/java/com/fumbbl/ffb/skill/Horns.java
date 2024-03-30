package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with horns may use them to butt an opponent. This adds 1 to the
 * player's Strength when he makes a block. However, the player may only use
 * this ability as part of a Blitz, and only if he has moved at least one square
 * before he makes the block (standing up at the start of your Action does not
 * count!). If the player has the Frenzy skill, then the Horns bonus applies on
 * the second block if it applied on the first.
 */
@RulesCollection(Rules.COMMON)
public class Horns extends Skill {

	public Horns() {
		super("Horns", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.addStrengthOnBlitz);

	}

}

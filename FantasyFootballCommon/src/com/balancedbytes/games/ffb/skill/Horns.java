package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

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
	public void postConstruct(ModifierDictionary dictionary) {
		registerProperty(NamedProperties.addStrengthOnBlitz);

	}

}

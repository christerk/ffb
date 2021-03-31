package com.balancedbytes.games.ffb.skill.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;

/**
 * The player has a long, thick tail which he can use to trip up opposing
 * players. To represent this, opposing players must subtract 1 from the D6 roll
 * if they attempt to dodge out of any of the player's tackle zones.
 */
@RulesCollection(Rules.BB2016)
public class PrehensileTail extends Skill {

	public PrehensileTail() {
		super("Prehensile Tail", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.makesDodgingHarder);
	}
}

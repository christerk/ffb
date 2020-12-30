package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * During each Start of a Drive sequence after Step 2 but before Step 3, you may
 * remove D3 payers with this Trait from the Reserves box of your dugout and set
 * them up on the pitch, allowing you to set up more than the usual 11 players.
 * These extra players may not be placed on the Line of Scrimmage or in a Wide
 * Zone.
 */
@RulesCollection(Rules.COMMON)
public class Swarming extends Skill {

	public Swarming() {
		super("Swarming", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canSneakExtraPlayersOntoPitch);

	}

}

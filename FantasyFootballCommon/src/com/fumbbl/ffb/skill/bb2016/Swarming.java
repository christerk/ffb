package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * During each Start of a Drive sequence after Step 2 but before Step 3, you may
 * remove D3 payers with this Trait from the Reserves box of your dugout and set
 * them up on the pitch, allowing you to set up more than the usual 11 players.
 * These extra players may not be placed on the Line of Scrimmage or in a Wide
 * Zone.
 */
@RulesCollection(Rules.BB2016)
public class Swarming extends Skill {

	public Swarming() {
		super("Swarming", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canSneakExtraPlayersOntoPitch);

	}

}

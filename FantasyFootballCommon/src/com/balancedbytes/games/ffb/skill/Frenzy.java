package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

/**
 * A player with this skill is a slavering psychopath who attacks his opponents
 * in an uncontrollable rage. Unless otherwise overridden, this skill must
 * always be used. When making a block, a player with this skill must always
 * follow up if they can. If a 'Pushed' or 'Defender Stumbles' result was
 * chosen, the player must immediately throw a second block against the same
 * opponent so long as they are both still standing and adjacent. If possible
 * the player must also follow up this second block. If the frenzied player is
 * performing a Blitz Action then he must pay a square of Movement and must make
 * the second block unless he has no further normal movement and cannot go for
 * it again.
 */
@RulesCollection(Rules.COMMON)
public class Frenzy extends Skill {

	public Frenzy() {
		super("Frenzy", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.forceFollowup);
		registerProperty(NamedProperties.forceSecondBlock);
	}

}

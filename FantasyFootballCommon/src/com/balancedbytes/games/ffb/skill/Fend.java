package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

/**
 * This player is very skilled at holding off would-be attackers. Opposing
 * players may not follow-up blocks made against this player even if the Fend
 * player is Knocked Down. The opposing player may still continue moving after
 * blocking if he had declared a Blitz Action.
 */
@RulesCollection(Rules.COMMON)
public class Fend extends Skill {

	public Fend() {
		super("Fend", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventOpponentFollowingUp);
	}

}

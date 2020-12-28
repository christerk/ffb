package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * A player with this skill uses his great strength and prowess to grab his
 * opponent and throw him around. To represent this, only while making a Block
 * Action, if his block results in a push back he may choose any empty square
 * adjacent to his opponent to push back his opponent. When making a Block or
 * Blitz Action, Grab and Side Step will cancel each other out and the standard
 * pushback rules apply. Grab will not work if there are no empty adjacent
 * squares. A player with the Grab skill can never learn or gain the Frenzy
 * skill through any means. Likewise, a player with the Frenzy skill can never
 * learn or gain the Grab skill through any means.
 */
public class Grab extends Skill {

	public Grab() {
		super("Grab", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canPushBackToAnySquare);
		registerProperty(new CancelSkillProperty(SkillConstants.SIDE_STEP));
	}

}

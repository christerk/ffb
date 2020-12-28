package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;

/**
 * A player with a Monstrous Mouth is allowed to re-roll the D6 if they fail a
 * Catch roll. It also allows the player to re-roll the D6 if they drop a
 * hand-off or fail to make an interception. In addition, the Strip Ball skill
 * will not work against a player with a Monstrous Mouth.
 */
public class MonstrousMouth extends Skill {

	public MonstrousMouth() {
		super("Monstrous Mouth", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(SkillConstants.STRIP_BALL));
		
		registerRerollSource(ReRolledActions.CATCH, ReRollSources.MONSTROUS_MOUTH);
	}

}

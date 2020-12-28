package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * A player on the receiving team that is not on the Line of Scrimmage or in an
 * opposing tackle zone may use this skill when the ball has been kicked. It
 * allows the player to move up to 3 squares after the ball has been scattered
 * but before rolling on the Kick-Off table. Only one player may use this skill
 * each kick-off. This skill may not be used for a touchback kick-off and does
 * not allow the player to cross into the opponent's half of the pitch.
 */
public class KickOffReturn extends Skill {

	public KickOffReturn() {
		super("Kick-Off Return", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMoveDuringKickOffScatter);

	}

}

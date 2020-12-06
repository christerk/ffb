package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * A player with this skill is capable of psyching themselves up so that they
 * can take on even the very strongest opponent. The skill only works when the
 * player attempts to block an opponent who is stronger than himself. When the
 * skill is used, the coach of the player with the Dauntless skill rolls a D6
 * and adds it to his strength. If the total is equal to or lower than the
 * opponent's Strength, the player must block using his normal Strength. If the
 * total is greater, then the player with the Dauntless skill counts as having a
 * Strength equal to his opponent's when he makes the block. The strength of
 * both players is calculated before any defensive or offensive assists are
 * added but after all other modifiers.
 */
public class Dauntless extends Skill {

	public Dauntless() {
		super("Dauntless", SkillCategory.GENERAL);

		registerProperty(NamedProperties.canRollToMatchOpponentsStrength);
	}

}

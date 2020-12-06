package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * The player has a powerful telepathic ability that he can use to stun an
 * opponent into immobility. The player may use hypnotic gaze at the end of his
 * Move Action on one opposing player who is in an adjacent square. Make an
 * Agility roll for the player with hypnotic gaze, with a -1 modifier for each
 * opposing tackle zone on the player with hypnotic gaze other than the
 * victim's. If the Agility roll is successful, then the opposing player loses
 * his tackle zones and may not catch, intercept or pass the ball, assist
 * another player on a block or foul, or move voluntarily until the start of his
 * next action or the drive ends. If the roll fails, then the hypnotic gaze has
 * no effect.
 */
public class HypnoticGaze extends Skill {

	public HypnoticGaze() {
		super("Hypnotic Gaze", SkillCategory.EXTRAORDINARY);

		registerProperty(NamedProperties.inflictsConfusion);
	}

}

package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.InterceptionModifiers;
import com.balancedbytes.games.ffb.LeapModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;

/**
 * The player is allowed to add 1 to the D6 roll whenever he attempts to
 * intercept or uses the Leap skill. In addition, the Safe Throw skill may not
 * be used to affect any Interception rolls made by this player.
 */
public class VeryLongLegs extends Skill {

	public VeryLongLegs() {
		super("Very Long Legs", SkillCategory.MUTATION);

		registerProperty(new CancelSkillProperty(SkillConstants.SAFE_THROW));

		registerModifier(LeapModifiers.VERY_LONG_LEGS);
		registerModifier(InterceptionModifiers.VERY_LONG_LEGS);
	}

}
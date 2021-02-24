package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.LeapModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * The player is allowed to add 1 to the D6 roll whenever he attempts to
 * intercept or uses the Leap skill. In addition, the Safe Throw skill may not
 * be used to affect any Interception rolls made by this player.
 */
@RulesCollection(Rules.COMMON)
public class VeryLongLegs extends Skill {

	public VeryLongLegs() {
		super("Very Long Legs", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(SkillConstants.SAFE_THROW));

		registerModifier(new LeapModifier("Very Long Legs", -1, ModifierType.REGULAR));
		registerModifier(new InterceptionModifier("Very Long Legs", -1, ModifierType.REGULAR));
	}

}

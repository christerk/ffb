package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * The player is allowed to add 1 to the D6 roll whenever he attempts to
 * intercept or uses the Leap skill. In addition, the Safe Throw skill may not
 * be used to affect any Interception rolls made by this player.
 */
@RulesCollection(Rules.BB2016)
public class VeryLongLegs extends Skill {

	public VeryLongLegs() {
		super("Very Long Legs", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.canCancelInterceptions));

		registerModifier(new JumpModifier("Very Long Legs", -1, ModifierType.REGULAR));
		registerModifier(new InterceptionModifier("Very Long Legs", -1, ModifierType.REGULAR));
	}

}

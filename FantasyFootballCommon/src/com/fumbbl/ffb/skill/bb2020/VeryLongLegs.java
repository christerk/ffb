package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * The player is allowed to add 1 to the D6 roll whenever he attempts to
 * intercept or uses the Leap skill. In addition, the Safe Throw skill may not
 * be used to affect any Interception rolls made by this player.
 */
@RulesCollection(Rules.BB2020)
public class VeryLongLegs extends Skill {

	public VeryLongLegs() {
		super("Very Long Legs", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.canForceInterceptionRerollOfLongPasses));

		registerModifier(new JumpModifier("Very Long Legs", -1, ModifierType.DEPENDS_ON_SUM_OF_OTHERS) {
			@Override
			public boolean appliesToContext(Skill skill, JumpContext context) {
				if (context.getAccumulatedModifiers() > 1) {
					context.addModififerValue(getModifier());
					return true;
				}
				return false;
			}
		});

		registerModifier(new InterceptionModifier("Very Long Legs", -2, ModifierType.REGULAR));
	}

}

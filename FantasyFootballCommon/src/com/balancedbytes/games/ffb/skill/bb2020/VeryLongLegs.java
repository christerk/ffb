package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.property.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

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

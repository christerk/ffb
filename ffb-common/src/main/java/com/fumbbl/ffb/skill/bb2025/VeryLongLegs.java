package com.fumbbl.ffb.skill.bb2025;

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
 * This player may apply a +1 modifier to the Agility Test whenever they 
 * attempt to Leap or Jump, and may apply a +2 modifier to the Agility Test 
 * whenever they attempt to Intercept the ball.
 * Additionally, this player ignores the Cloud Burster skill.
 */
@RulesCollection(Rules.BB2025)
public class VeryLongLegs extends Skill {

	public VeryLongLegs() {
		super("Very Long Legs", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.passesAreNotIntercepted));

		registerModifier(new JumpModifier("Very Long Legs", -1, ModifierType.REGULAR));

		registerModifier(new InterceptionModifier("Very Long Legs", -2, ModifierType.REGULAR));
	}

}

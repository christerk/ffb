package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

/**
 * The player ignores modifiers for enemy tackle zones when he attempts to pass,
 * catch or intercept.
 */
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class NervesOfSteel extends Skill {

	public NervesOfSteel() {
		super("Nerves of Steel", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PassModifier("Nerves of Steel", "0 tackle zones due to Nerves of Steel",0, ModifierType.REGULAR) {
			@Override
			public boolean isModifierIncluded() {
				return true;
			}
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return !context.isTtm();
			}
		});
		registerModifier(new InterceptionModifier("Nerves of Steel", "0 tackle zones due to Nerves of Steel", 0, ModifierType.REGULAR) {
			@Override
			public boolean isModifierIncluded() {
				return true;
			}
		});
		registerModifier(new CatchModifier("Nerves of Steel","0 for tackle zones due to Nerves of Steel",0, ModifierType.REGULAR) {
			@Override
			public boolean isModifierIncluded() {
				return true;
			}
		});

		registerProperty(NamedProperties.ignoreTacklezonesWhenPassing);
		registerProperty(NamedProperties.ignoreTacklezonesWhenCatching);	
	}
}

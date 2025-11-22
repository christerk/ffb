package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticInjuryModifier;

/**
 * This player treats a roll of 8 on the Injury table, after any modifiers have
 * been applied, as a Stunned result rather than a KO'd result. This skill may
 * be used even if the player is Prone or Stunned.
 */
@RulesCollection(Rules.COMMON)
public class ThickSkull extends Skill {

	public ThickSkull() {
		super("Thick Skull", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.convertKOToStunOn8);
		registerModifier(new StaticInjuryModifier("Thick Skull", 0, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return false;
			}
		});
	}

}

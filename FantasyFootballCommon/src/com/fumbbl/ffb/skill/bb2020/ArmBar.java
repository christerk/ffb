package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifier;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ArmBar extends Skill {

	public ArmBar() {
		super("Arm Bar", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnDodge);
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnJump);
		registerModifier(new StaticArmourModifier("Arm Bar", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});
		registerModifier(new StaticInjuryModifier("Arm Bar", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return false;
			}
		});

	}
}

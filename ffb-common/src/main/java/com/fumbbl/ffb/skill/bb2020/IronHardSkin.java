package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

@RulesCollection(RulesCollection.Rules.BB2020)
public class IronHardSkin extends Skill {
	public IronHardSkin() {
		super("Iron Hard Skin", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.reducesArmourToFixedValue));
		registerProperty(NamedProperties.ignoresArmourModifiersFromFouls);
		registerProperty(NamedProperties.ignoresArmourModifiersFromSkills);
		registerProperty(NamedProperties.ignoresArmourModifiersFromSpecialEffects);
		registerModifier(new StaticArmourModifier("Iron Hard Skin", 0, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});
	}
}

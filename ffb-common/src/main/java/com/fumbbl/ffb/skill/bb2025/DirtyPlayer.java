package com.fumbbl.ffb.skill.bb2025;

import java.util.Arrays;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;

/**
 * When this player performs a Foul Action, they may apply a +1 modifier to 
 * either the Armour Roll or Injury Roll. This modifier may be applied after 
 * the roll has been made
 */
@RulesCollection(Rules.BB2025)
public class DirtyPlayer extends Skill {

	public DirtyPlayer() {
		super("Dirty Player", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Dirty Player", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return context.isFoul();
			}
		});
		registerModifier(new StaticInjuryModifierAttacker("Dirty Player", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context)
					&&(context.isFoul()
					&& Arrays.stream(context.getInjuryContext().getArmorModifiers())
					.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnFoul)));
			}
		});
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnFoul);

	}

}

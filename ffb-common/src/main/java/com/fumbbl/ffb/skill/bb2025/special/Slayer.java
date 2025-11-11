package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;

/**
 * Once per game, when an opposition Big Guy is Knocked Down as a result of a Block Action performed 
 * by Grim, you may apply an additional +1 modifier to either the Armour Roll or the Injury Roll. 
 * This modifier may be applied after the roll has been made.
 */

@RulesCollection(Rules.BB2025)
public class Slayer extends Skill {
	public Slayer() {
		super("Slayer", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Slayer", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});

		registerModifier(new StaticInjuryModifierAttacker("Slayer", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return false;
			}
		});
	}
}

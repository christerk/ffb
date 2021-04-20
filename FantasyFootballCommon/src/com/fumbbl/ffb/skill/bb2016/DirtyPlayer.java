package com.fumbbl.ffb.skill.bb2016;

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
 * A player with this skill has trained long and hard to learn every dirty trick
 * in the book. Add 1 to any Armour roll or Injury roll made by a player with
 * this skill when they make a Foul as part of a Foul Action. Note that you may
 * only modify one of the dice rolls, so if you decide to use Dirty Player to
 * modify the Armour roll, you may not modify the Injury roll as well.
 */
@RulesCollection(Rules.BB2016)
public class DirtyPlayer extends Skill {

	public DirtyPlayer() {
		super("Dirty Player", SkillCategory.GENERAL);
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

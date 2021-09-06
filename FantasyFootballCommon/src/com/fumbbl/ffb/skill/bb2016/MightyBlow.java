package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;

import java.util.Arrays;

/**
 * Add 1 to any Armour or Injury roll made by a player with this skill when an
 * opponent is Knocked Down by this player during a block. Note that you only
 * modify one of the dice rolls, so if you decide to use Mighty Blow to modify
 * the Armour roll, you may not modify the Injury roll as well. Mighty Blow
 * cannot be used with the Stab or Chainsaw skills.
 */
@RulesCollection(Rules.BB2016)
public class MightyBlow extends Skill {

	public MightyBlow() {
		super("Mighty Blow", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Mighty Blow", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return super.appliesToContext(context) && !context.isFoul();
			}
		});
		registerModifier(new StaticInjuryModifierAttacker("Mighty Blow", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context)
					&& !context.isFoul()
					&& Arrays.stream(context.getInjuryContext().getArmorModifiers())
					.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock));
			}
		});
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock);
	}

}

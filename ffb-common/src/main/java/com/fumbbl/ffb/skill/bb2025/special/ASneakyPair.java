package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ASneakyPair extends Skill {
	public ASneakyPair() {
		super("A Sneaky Pair", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier(getName(), 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return super.appliesToContext(context) && (context.isFoul() || context.isStab())
					&& UtilPlayer.partnerMarksDefender(context.getGame(), context.getDefender(), ASneakyPair.this);
			}
		});

		registerModifier(new StaticInjuryModifierAttacker(getName(), 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context)
					&& (context.isFoul() || context.isStab())
					&& UtilPlayer.partnerMarksDefender(context.getGame(), context.getDefender(), ASneakyPair.this)
					&& Arrays.stream(context.getInjuryContext().getArmorModifiers())
					.noneMatch(mod -> mod.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryWithPartner));
			}
		});

		registerProperty(NamedProperties.affectsEitherArmourOrInjuryWithPartner);
	}
}

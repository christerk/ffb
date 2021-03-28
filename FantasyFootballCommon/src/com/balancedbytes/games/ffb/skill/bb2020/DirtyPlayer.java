package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.skill.SkillValueEvaluator;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierContext;
import com.balancedbytes.games.ffb.modifiers.InjuryModifierContext;
import com.balancedbytes.games.ffb.modifiers.VariableArmourModifier;
import com.balancedbytes.games.ffb.modifiers.VariableInjuryModifierAttacker;

import java.util.Arrays;

/**
 * A player with this skill has trained long and hard to learn every dirty trick
 * in the book. Add 1 to any Armour roll or Injury roll made by a player with
 * this skill when they make a Foul as part of a Foul Action. Note that you may
 * only modify one of the dice rolls, so if you decide to use Dirty Player to
 * modify the Armour roll, you may not modify the Injury roll as well.
 */
@RulesCollection(Rules.BB2020)
public class DirtyPlayer extends Skill {

	public DirtyPlayer() {
		super("Dirty Player", SkillCategory.GENERAL, 1);
	}

	@Override
	public void postConstruct() {
		registerModifier(new VariableArmourModifier("Dirty Player",false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return context.isFoul();
			}
		});
		registerModifier(new VariableInjuryModifierAttacker("Dirty Player",false) {
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

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.MODIFIER;
	}

}

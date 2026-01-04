package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.VariableArmourModifier;
import com.fumbbl.ffb.modifiers.VariableInjuryModifierAttacker;

import java.util.Arrays;

/**
 * Add 1 to any Armour or Injury roll made by a player with this skill when an
 * opponent is Knocked Down by this player during a block. Note that you only
 * modify one of the dice rolls, so if you decide to use Mighty Blow to modify
 * the Armour roll, you may not modify the Injury roll as well. Mighty Blow
 * cannot be used with the Stab or Chainsaw skills.
 */
@RulesCollection(Rules.BB2025)
public class MightyBlow extends Skill {

	public MightyBlow() {
		super("Mighty Blow", SkillCategory.STRENGTH, 1);
	}

	@Override
	public void postConstruct() {
		registerModifier(new VariableArmourModifier("Mighty Blow", false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				PlayerState playerState = context.getGame().getFieldModel().getPlayerState(context.getAttacker());
				return super.appliesToContext(context)
					&& playerState != null && !playerState.isDistracted()
					&& !context.isStab()
					&& !context.isFoul();
			}
		});
		registerModifier(new VariableInjuryModifierAttacker("Mighty Blow", false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				PlayerState playerState = context.getGame().getFieldModel().getPlayerState(context.getAttacker());

				return super.appliesToContext(context)
					&& !context.isFoul()
					&& !context.isStab()
					&& !context.isVomitLike()
					&& playerState != null && !playerState.isDistracted()
					&& Arrays.stream(context.getInjuryContext().getArmorModifiers())
					.noneMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock));
			}
		});
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.MODIFIER;
	}
}

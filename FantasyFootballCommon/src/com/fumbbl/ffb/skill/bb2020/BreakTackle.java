package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.util.UtilCards;

/**
 * The player may add a bonus to his Dodge roll depending on his Strength.
 * A player with Strength 4 or less may add 1 while a player with Strength 5 or more may add 2.
 * This skill may only be used once per turn.
 */
@RulesCollection(Rules.BB2020)
public class BreakTackle extends Skill {

	public BreakTackle() {
		super("Break Tackle", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(
			new DodgeModifier("Break Tackle ST 5+", "Break Tackle", -2, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.getPlayer().getStrengthWithModifiers() >= 5 && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
				}
			});
		registerModifier(
			new DodgeModifier("Break Tackle ST 4-","Break Tackle", -1, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.getPlayer().getStrengthWithModifiers() <= 4 && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
				}
			});
	}

}

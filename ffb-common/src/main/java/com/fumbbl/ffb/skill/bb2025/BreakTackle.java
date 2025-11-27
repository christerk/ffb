package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.util.UtilCards;

/**
 * Once per Turn, when this player attempts to Dodge, they may apply 
 * a +1 modifier to the Agility Test if they have a Strength Characteristic of 3 or lower, 
 * a +2 modifier to the Agility Test if they have a Strength Characteristic of 4, or 
 * a +3 modifier to the Agility Test if they have a Strength Characteristic of 5 or higher.
 */
@RulesCollection(Rules.BB2025)
public class BreakTackle extends Skill {

	public BreakTackle() {
		super("Break Tackle", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(
			new DodgeModifier("Break Tackle ST 5+", "Break Tackle", -3, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.getPlayer().getStrengthWithModifiers() >= 5 && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
				}
			});
		registerModifier(
			new DodgeModifier("Break Tackle ST 4", "Break Tackle", -2, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.getPlayer().getStrengthWithModifiers() == 4 && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
				}
			});
		registerModifier(
			new DodgeModifier("Break Tackle ST 3-","Break Tackle", -1, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.getPlayer().getStrengthWithModifiers() <= 3 && (context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill));
				}
			});
	}

}

package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * Once per game, this player may apply a +1 modifier to an Agility Test they have made.
 * This modifier may be applied after the roll has been made.
 */

@RulesCollection(Rules.BB2025)
public class ConsummateProfessional extends Skill {
	public ConsummateProfessional() {
		super("Consummate Professional", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerModifier(
			new DodgeModifier("Consummate Professional", -1, ModifierType.REGULAR, false, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.isSkillSelected(skill);
				}
			});
	}
}

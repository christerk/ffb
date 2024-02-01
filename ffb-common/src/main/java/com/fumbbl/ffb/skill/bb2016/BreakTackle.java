package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.util.UtilCards;

/**
 * The player may use his Strength instead of his Agility when making a Dodge
 * roll. For example, a player with Strength 4 and Agility 2 would count as
 * having an Agility of 4 when making a Dodge roll. This skill may only be used
 * once per turn.
 */
@RulesCollection(Rules.BB2016)
public class BreakTackle extends Skill {

	public BreakTackle() {
		super("Break Tackle", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(
			new DodgeModifier("Break Tackle", 0, ModifierType.REGULAR, true) {
				@Override
				public boolean appliesToContext(Skill skill, DodgeContext context) {
					return context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getActingPlayer(), skill);
				}
			});
	}

}

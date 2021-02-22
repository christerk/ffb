package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.DodgeContext;
import com.balancedbytes.games.ffb.modifiers.DodgeModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * The player may use his Strength instead of his Agility when making a Dodge
 * roll. For example, a player with Strength 4 and Agility 2 would count as
 * having an Agility of 4 when making a Dodge roll. This skill may only be used
 * once per turn.
 */
@RulesCollection(Rules.COMMON)
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
					return context.isUseBreakTackle() || UtilCards.hasUnusedSkill(context.getGame(), context.getActingPlayer(), skill);
				}
			});
	}

}

package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * Having two heads enables this player to watch where he is going and the
 * opponent trying to make sure he does not get there at the same time. Add 1 to
 * all Dodge rolls the player makes.
 */
@RulesCollection(Rules.COMMON)
public class TwoHeads extends Skill {

	public TwoHeads() {
		super("Two Heads", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerModifier(new DodgeModifier("Two Heads", -1, ModifierType.REGULAR));
	}
}

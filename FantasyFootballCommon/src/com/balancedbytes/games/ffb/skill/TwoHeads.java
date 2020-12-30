package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.DodgeModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

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
		registerModifier(DodgeModifiers.TWO_HEADS);
	}
}

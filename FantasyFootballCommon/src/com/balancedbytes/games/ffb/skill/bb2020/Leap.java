package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

/**
 * A player with the Leap skill is allowed to also jump over squares that are
 * empty or occupied by a standing player. Additionally modifiers from being
 * marked can be reduced by 1 to a minimum of -1.
 */
@RulesCollection(Rules.BB2020)
public class Leap extends Skill {

	public Leap() {
		super("Leap", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerModifier(new JumpModifier("Leap", 1, ModifierType.REGULAR)); //TODO only apply if marked by more than one
	}

}

package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.InjuryModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * A player with this skill has trained long and hard to learn every dirty trick
 * in the book. Add 1 to any Armour roll or Injury roll made by a player with
 * this skill when they make a Foul as part of a Foul Action. Note that you may
 * only modify one of the dice rolls, so if you decide to use Dirty Player to
 * modify the Armour roll, you may not modify the Injury roll as well.
 */
@RulesCollection(Rules.COMMON)
public class DirtyPlayer extends Skill {

	public DirtyPlayer() {
		super("Dirty Player", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerModifier(ArmorModifiers.DIRTY_PLAYER);
		registerModifier(InjuryModifiers.DIRTY_PLAYER);

	}

}

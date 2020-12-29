package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * This player is armed with special stakes that are blessed to cause extra
 * damage to the Undead and those that work with them. This player may add 1 to
 * the Armour roll when they make a Stab attack against any player playing for a
 * Khemri, Necromantic, Undead or Vampire team.
 */
@RulesCollection(Rules.All)
public class Stakes extends Skill {

	public Stakes() {
		super("Stakes", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerModifier(ArmorModifiers.STAKES);

	}

}

package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * Some teams field players of great skill and ability. Other teams, however, do
 * not. Whilst most teams will hire capable players and pay them a fair wage,
 * some teams will happily take on the most useless of players to fill out their
 * ranks. Readily available, easily replaceable and usually willing to work for
 * a pittance, such players fill gaps in the rosters, but rarely do much more!
 * When calculating Team Value, the amount of gold pieces spent to purchase a
 * player with this skill is not included in the total.
 */
@RulesCollection(Rules.All)
public class Disposable extends Skill {

	public Disposable() {
		super("Disposable", SkillCategory.EXTRAORDINARY);
	}

}

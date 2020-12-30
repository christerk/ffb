package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The fans love seeing this player on the pitch so much that even the opposing
 * fans cheer for your team. For each player with Fan Favourite on the pitch
 * your team receives an additional +1 FAME modifier (see page 18) for any
 * Kick-Off table results, but not for the Winnings roll.
 */
@RulesCollection(Rules.COMMON)
public class FanFavourite extends Skill {

	public FanFavourite() {
		super("Fan Favourite", SkillCategory.EXTRAORDINARY);
	}

}

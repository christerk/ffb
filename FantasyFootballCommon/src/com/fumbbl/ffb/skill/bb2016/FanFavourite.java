package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The fans love seeing this player on the pitch so much that even the opposing
 * fans cheer for your team. For each player with Fan Favourite on the pitch
 * your team receives an additional +1 FAME modifier (see page 18) for any
 * Kick-Off table results, but not for the Winnings roll.
 */
@RulesCollection(Rules.BB2016)
public class FanFavourite extends Skill {

	public FanFavourite() {
		super("Fan Favourite", SkillCategory.EXTRAORDINARY);
	}

}

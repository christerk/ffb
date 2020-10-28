package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The fans love seeing this player on the pitch so much that even the
* opposing fans cheer for your team. For each player with Fan Favourite
* on the pitch your team receives an additional +1 FAME modifier (see
* page 18) for any Kick-Off table results, but not for the Winnings roll.
*/
public class FanFavourite extends ServerSkill {

  public FanFavourite() {
    super("Fan Favourite", SkillCategory.EXTRAORDINARY);
  }

}

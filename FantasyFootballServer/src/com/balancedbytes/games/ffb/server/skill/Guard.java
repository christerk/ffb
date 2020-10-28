package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with this skill assists an offensive or defensive block even if he
* is in another player's tackle zone. This skill may not be used to assist a
* foul.
*/
public class Guard extends ServerSkill {

  public Guard() {
    super("Guard", SkillCategory.STRENGTH);
  }

}

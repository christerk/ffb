package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* A player with this skill assists an offensive or defensive block even if he
* is in another player's tackle zone. This skill may not be used to assist a
* foul.
*/
public class Guard extends Skill {

  public Guard() {
    super("Guard", SkillCategory.STRENGTH);
  }

}

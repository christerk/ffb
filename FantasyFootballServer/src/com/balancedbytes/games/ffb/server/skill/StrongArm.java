package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player may add 1 to the D6 when he passes to Short, Long or Long
* Bomb range.
*/
public class StrongArm extends ServerSkill {

  public StrongArm() {
    super("Strong Arm", SkillCategory.STRENGTH);
  }

}

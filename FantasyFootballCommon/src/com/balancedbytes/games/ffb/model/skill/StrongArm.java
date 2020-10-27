package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player may add 1 to the D6 when he passes to Short, Long or Long
* Bomb range.
*/
public class StrongArm extends Skill {

  public StrongArm() {
    super("Strong Arm", SkillCategory.STRENGTH);
  }

}

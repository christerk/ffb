package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player may add 1 to the D6 roll when he passes.
*/
public class Accurate extends ServerSkill {

  public Accurate() {
    super("Accurate", SkillCategory.PASSING);
  }

}

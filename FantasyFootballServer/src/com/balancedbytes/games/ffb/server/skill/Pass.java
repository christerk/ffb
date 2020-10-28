package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with the Pass skill is allowed to re-roll the D6 if he throws an
* inaccurate pass or fumbles.
*/
public class Pass extends ServerSkill {

  public Pass() {
    super("Pass", SkillCategory.PASSING);
  }

}

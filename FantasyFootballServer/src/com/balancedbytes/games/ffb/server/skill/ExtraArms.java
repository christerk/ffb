package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with one or more extra arms may add 1 to any attempt to pick
* up, catch or intercept.
*/
public class ExtraArms extends ServerSkill {

  public ExtraArms() {
    super("Extra Arms", SkillCategory.MUTATION);
  }

}

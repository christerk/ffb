package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player ignores modifiers for enemy tackle zones when he attempts
* to pass, catch or intercept.
*/
public class NervesOfSteel extends ServerSkill {

  public NervesOfSteel() {
    super("Nerves of Steel", SkillCategory.PASSING);
  }

}

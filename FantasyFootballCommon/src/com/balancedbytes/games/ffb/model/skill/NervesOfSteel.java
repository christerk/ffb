package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player ignores modifiers for enemy tackle zones when he attempts
* to pass, catch or intercept.
*/
public class NervesOfSteel extends Skill {

  public NervesOfSteel() {
    super("Nerves of Steel", SkillCategory.PASSING);
  }

}

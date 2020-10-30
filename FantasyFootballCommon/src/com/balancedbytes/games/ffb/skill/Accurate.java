package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PassingModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player may add 1 to the D6 roll when he passes.
*/
public class Accurate extends Skill {

  public Accurate() {
    super("Accurate", SkillCategory.PASSING);
    
    registerModifier(PassingModifiers.ACCURATE);
  }

}

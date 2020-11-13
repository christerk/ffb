package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PassingModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* The player ignores modifiers for enemy tackle zones when he attempts
* to pass, catch or intercept.
*/
public class NervesOfSteel extends Skill {

  public NervesOfSteel() {
    super("Nerves of Steel", SkillCategory.PASSING);
    
    registerModifier(PassingModifiers.NERVES_OF_STEEL);
    
    registerProperty(NamedProperties.ignoreTacklezonesWhenPassing);
    registerProperty(NamedProperties.ignoreTacklezonesWhenCatching);
  }

}

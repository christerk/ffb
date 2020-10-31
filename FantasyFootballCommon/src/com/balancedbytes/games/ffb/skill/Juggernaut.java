package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* A player with this skill is virtually impossible to stop once he is in motion.
* If this player takes a Blitz Action, then opposing players may not use
* their Fend, Stand Firm or Wrestle skills against blocks, and he may
* choose to treat a "Both Down" result as if a "Pushed" result has been
* rolled instead.
*/
public class Juggernaut extends Skill {

  public Juggernaut() {
    super("Juggernaut", SkillCategory.STRENGTH);
    
    registerProperty(new CancelSkillProperty(SkillConstants.JUGGERNAUT));
    registerProperty(new CancelSkillProperty(SkillConstants.WRESTLE));
    registerProperty(new CancelSkillProperty(SkillConstants.STAND_FIRM));
    registerProperty(new CancelSkillProperty(SkillConstants.FEND));
    
  }

  @Override
  public String[] getSkillUseDescription() {
    return new String[] {
        "Using Juggernaut will convert the BOTH DOWN Block Result into a PUSHBACK."
    };
  }
}

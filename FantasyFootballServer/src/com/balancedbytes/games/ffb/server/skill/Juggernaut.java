package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.ServerSkillConstants;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.model.modifier.CancelSkillProperty;

/**
* A player with this skill is virtually impossible to stop once he is in motion.
* If this player takes a Blitz Action, then opposing players may not use
* their Fend, Stand Firm or Wrestle skills against blocks, and he may
* choose to treat a "Both Down" result as if a "Pushed" result has been
* rolled instead.
*/
public class Juggernaut extends ServerSkill {

  public Juggernaut() {
    super("Juggernaut", SkillCategory.STRENGTH);
    
    registerProperty(new CancelSkillProperty(ServerSkillConstants.JUGGERNAUT));
  }

  @Override
  public String[] getSkillUseDescription() {
    return new String[] {
        "Using Juggernaut will convert the BOTH DOWN Block Result into a PUSHBACK."
    };
  }
}

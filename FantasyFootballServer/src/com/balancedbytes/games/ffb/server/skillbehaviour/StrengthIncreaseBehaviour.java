package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthIncrease;

public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
  public StrengthIncreaseBehaviour() {
    super();
    
    registerModifier(player -> player.setStrength(player.getStrength() + 1));
  }
}

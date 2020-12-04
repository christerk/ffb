package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.ArmourDecrease;

public class ArmourDecreaseBehaviour extends SkillBehaviour<ArmourDecrease> {
  public ArmourDecreaseBehaviour() {
    super();
    
    registerModifier(player -> player.setArmour(player.getArmour() - 1));
  }
}

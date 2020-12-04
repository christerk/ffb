package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementIncrease;

public class MovementIncreaseBehaviour extends SkillBehaviour<MovementIncrease> {
  public MovementIncreaseBehaviour() {
    super();
    
    registerModifier(player -> player.setMovement(player.getMovement() + 1));
  }
}

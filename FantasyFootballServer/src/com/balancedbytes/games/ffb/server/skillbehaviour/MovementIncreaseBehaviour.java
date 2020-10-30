package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementIncrease;

public class MovementIncreaseBehaviour extends SkillBehaviour<MovementIncrease> {
  public MovementIncreaseBehaviour() {
    super();
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setMovement(player.getMovement() + 1);
      }
    });
  }
}

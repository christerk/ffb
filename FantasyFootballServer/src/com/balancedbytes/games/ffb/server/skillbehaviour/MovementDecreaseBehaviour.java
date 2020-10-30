package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementDecrease;

public class MovementDecreaseBehaviour extends SkillBehaviour<MovementDecrease> {
  public MovementDecreaseBehaviour() {
    super();
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setMovement(player.getMovement() - 1);
      }
    });
  }
}

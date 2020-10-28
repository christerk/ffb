package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

public class MovementDecrease extends ServerSkill {

  public MovementDecrease() {
    super("-MA", SkillCategory.STAT_DECREASE);
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setMovement(player.getMovement() - 1);
      }
    });
  }

}

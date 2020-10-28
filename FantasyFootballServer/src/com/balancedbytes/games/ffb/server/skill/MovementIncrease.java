package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

public class MovementIncrease extends ServerSkill {

  public MovementIncrease() {
    super("+MA", SkillCategory.STAT_INCREASE);
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setMovement(player.getMovement() + 1);
      }
    });
  }

  @Override
  public int getCost(Player player) {
    return 30000;
  }
}

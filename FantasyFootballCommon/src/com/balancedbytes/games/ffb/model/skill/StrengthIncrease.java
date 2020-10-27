package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;

public class StrengthIncrease extends Skill {

  public StrengthIncrease() {
    super("+ST", SkillCategory.STAT_INCREASE);
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setStrength(player.getStrength() + 1);
      }
    });
  }

  @Override
  public int getCost(Player player) {
    return 50000;
  }
  
}

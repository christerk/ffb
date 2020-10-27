package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;

public class StrengthDecrease extends Skill {

  public StrengthDecrease() {
    super("-ST", SkillCategory.STAT_DECREASE);
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setStrength(player.getStrength() - 1);
      }
    });
  }

}

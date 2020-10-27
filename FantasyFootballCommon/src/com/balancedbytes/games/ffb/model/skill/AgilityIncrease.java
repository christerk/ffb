package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.model.Skill;

public class AgilityIncrease extends Skill {

  public AgilityIncrease() {
    super("+AG", SkillCategory.STAT_INCREASE);
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setAgility(player.getAgility() + 1);
      }
    });
  }

  @Override
  public int getCost(Player player) {
    return 40000;
  }
}

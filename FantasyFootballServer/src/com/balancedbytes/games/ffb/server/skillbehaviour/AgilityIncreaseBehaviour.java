package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.AgilityIncrease;

public class AgilityIncreaseBehaviour extends SkillBehaviour<AgilityIncrease> {
  public AgilityIncreaseBehaviour() {
    super();
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setAgility(player.getAgility() + 1);
      }
    });
  }
}

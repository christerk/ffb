package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthIncrease;

public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
  public StrengthIncreaseBehaviour() {
    super();
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setStrength(player.getStrength() + 1);
      }
    });
  }
}

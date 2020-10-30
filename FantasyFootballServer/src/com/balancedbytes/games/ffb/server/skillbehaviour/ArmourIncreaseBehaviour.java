package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerModifier;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.ArmourIncrease;

public class ArmourIncreaseBehaviour extends SkillBehaviour<ArmourIncrease> {
  public ArmourIncreaseBehaviour() {
    super();
    
    registerModifier(new PlayerModifier() {
      @Override
      public void apply(Player player) {
        player.setArmour(player.getArmour() + 1);
      }
    });
  }
}

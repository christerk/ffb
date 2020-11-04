package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
* This player has the quickness and finesse to stick the boot to a downed
* opponent without drawing a referee's attention unless he hears the
* armour crack. During a Foul Action a player with this skill is not ejected
* for rolling doubles on the Armour roll unless the Armour roll was
* successful.
*/
public class SneakyGit extends Skill {

  public SneakyGit() {
    super("Sneaky Git", SkillCategory.AGILITY);
    
    registerProperty(NamedProperties.canAlwaysAssistFouls);
  }

}

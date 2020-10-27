package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* Opposing players who are standing in any of this player's tackle zones
* are not allowed to use their Dodge skill if they attempt to dodge out of
* any of the player's tackle zones, nor may they use their Dodge skill if the
* player throws a block at them and uses the Tackle skill.
*/
public class Tackle extends Skill {

  public Tackle() {
    super("Tackle", SkillCategory.GENERAL);
  }

}

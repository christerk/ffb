package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with a Monstrous Mouth is allowed to re-roll the D6 if they fail
* a Catch roll. It also allows the player to re-roll the D6 if they drop a
* hand-off or fail to make an interception. In addition, the Strip Ball skill
* will not work against a player with a Monstrous Mouth.
*/
public class MonstrousMouth extends ServerSkill {

  public MonstrousMouth() {
    super("Monstrous Mouth", SkillCategory.EXTRAORDINARY);
  }

}

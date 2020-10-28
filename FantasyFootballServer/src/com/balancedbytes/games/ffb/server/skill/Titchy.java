package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* Titchy players tend to be even smaller and more nimble than other
* Stunty players. To represent this, the player may add 1 to any Dodge roll
* he attempts. On the other hand, while opponents do have to dodge to
* leave any of a Titchy player's tackle zones, Titchy players are so small
* that they do not exert a -1 modifier when opponents dodge into any of
* their tackle zones.
*/
public class Titchy extends ServerSkill {

  public Titchy() {
    super("Titchy", SkillCategory.EXTRAORDINARY);
  }

}

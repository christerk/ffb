package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* Wild Animals are uncontrollable creatures that rarely do exactly what a
* coach wants of them. In fact, just about all you can really rely on them to
* do is lash out at opposing players that move too close to them! To
* represent this, immediately after declaring an Action with a Wild Animal,
* roll a D6, adding 2 to the roll if taking a Block or Blitz Action. On a roll of
* 1-3, the Wild Animal does not move and roars in rage instead, and the
* Action is wasted.
*/
public class WildAnimal extends ServerSkill {

  public WildAnimal() {
    super("Wild Animal", SkillCategory.EXTRAORDINARY);
  }

}

package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player may use his Strength instead of his Agility when making a
* Dodge roll. For example, a player with Strength 4 and Agility 2 would
* count as having an Agility of 4 when making a Dodge roll. This skill may
* only be used once per turn.
*/
public class BreakTackle extends ServerSkill {

  public BreakTackle() {
    super("Break Tackle", SkillCategory.STRENGTH);
  }

}

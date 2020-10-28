package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with the Block skill is proficient at knocking opponents down.
* The Block skill, if used, affects the results rolled with the Block dice, as
* explained in the Blocking rules.
*/
public class Block extends ServerSkill {

  public Block() {
    super("Block", SkillCategory.GENERAL);
  }

}

package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* This player treats a roll of 8 on the Injury table, after any modifiers have
* been applied, as a Stunned result rather than a KO'd result. This skill
* may be used even if the player is Prone or Stunned.
*/
public class ThickSkull extends ServerSkill {

  public ThickSkull() {
    super("Thick Skull", SkillCategory.STRENGTH);
  }

}

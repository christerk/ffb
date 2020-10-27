package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player may attempt to move up to three extra squares rather than
* the normal two when Going For It (see page 20). His coach must still roll
* to see if the player is Knocked Down in each extra square he enters.
*/
public class Sprint extends Skill {

  public Sprint() {
    super("Sprint", SkillCategory.AGILITY);
  }

}

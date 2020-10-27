package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player is allowed to add 1 to the D6 roll whenever he attempts to
* intercept or uses the Leap skill. In addition, the Safe Throw skill may not
* be used to affect any Interception rolls made by this player.
*/
public class VeryLongLegs extends Skill {

  public VeryLongLegs() {
    super("Very Long Legs", SkillCategory.MUTATION);
  }

}

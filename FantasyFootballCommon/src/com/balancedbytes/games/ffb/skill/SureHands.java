package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;

/**
* A player with the Sure Hands skill is allowed to re-roll the D6 if he fails to
* pick up the ball. In addition, the Strip Ball skill will not work against a
* player with this skill.
*/
public class SureHands extends Skill {

  public SureHands() {
    super("Sure Hands", SkillCategory.GENERAL);
    
    registerProperty(new CancelSkillProperty(SkillConstants.STRIP_BALL));

    registerRerollSource(ReRolledAction.PICK_UP, ReRollSource.SURE_HANDS);
  }

}

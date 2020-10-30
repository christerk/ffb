package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* A player with the Sure Hands skill is allowed to re-roll the D6 if he fails to
* pick up the ball. In addition, the Strip Ball skill will not work against a
* player with this skill.
*/
public class SureHands extends Skill {

  public SureHands() {
    super("Sure Hands", SkillCategory.GENERAL);
  }

}

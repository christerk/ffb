package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* When a player with this skill blocks an opponent with the ball, applying a
* "Pushed" or "Defender Stumbles" result will cause the opposing player to
* drop the ball in the square that they are pushed to, even if the opposing
* player is not Knocked Down.
*/
public class StripBall extends Skill {

  public StripBall() {
    super("Strip Ball", SkillCategory.GENERAL);
  }

}

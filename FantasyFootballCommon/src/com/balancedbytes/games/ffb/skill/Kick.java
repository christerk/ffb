package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player is an expert at kicking the ball and can place the kick with
* great precision. In order to use this skill the player must be set up on the
* pitch when his team kicks off. The player may not be set up in either
* wide zone or on the line of scrimmage. Only if all these conditions are
* met is the player then allowed to take the kick-off. Because his kick is so
* accurate, you may choose to halve the number of squares that the ball
* scatters on kick-off, rounding any fractions down (i.e., 1 = 0, 2-3 = 1,
* 4-5 = 2, 6 = 3).
*/
public class Kick extends Skill {

  public Kick() {
    super("Kick", SkillCategory.GENERAL);
  }

}

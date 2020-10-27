package com.balancedbytes.games.ffb.model.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* This player is without doubt one of the dimmest creatures to ever take to
* a Blood Bowl pitch (which considering the IQ of most other players, is
* really saying something!). Because of this you must roll a D6 immediately
* after declaring an Action for the player, but before taking the Action. If
* there are one or more players from the same team standing adjacent to
* the Really Stupid player's square, and who aren't "Really Stupid", then add
* 2 to the D6 roll. On a result of 1-3 they stand around trying to remember
* what it is they're meant to be doing. The player can't do anything for the
* turn, and the player's team loses the declared Action for that turn (for
* example if a Really Stupid player declares a Blitz Action and fails the
* Really Stupid roll, then the team cannot declare another Blitz Action that
* turn). The player loses his tackle zones and may not catch, intercept or
* pass the ball, assist another player on a block or foul, or voluntarily move
* until he manages to roll a successful result for a Really Stupid roll
* at the start of a future Action or the drive ends.
*/
public class ReallyStupid extends Skill {

  public ReallyStupid() {
    super("Really Stupid", SkillCategory.EXTRAORDINARY);
  }

}

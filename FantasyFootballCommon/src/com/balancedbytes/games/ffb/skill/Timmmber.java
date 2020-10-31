package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* This player spends so much time on the floor that their team-mates have
* developed a knack for helping them up. If a player with this skill attempts
* to stand up after being knocked over, other players from their team can
* assist if they are adjacent, standing and not in any enemy tackle zones.
* Each player that assists in this way adds 1 to the result of the dice roll
* to see whether the player stands up, but remember that a 1 is always a
* failure, no matter how many players are helping! Assisting a player to stand
* up does not count as an Action, and a player can assist regardless of whether
* they have taken an Action.
*/
public class Timmmber extends Skill {

  public Timmmber() {
    super("Timmm-ber!", SkillCategory.EXTRAORDINARY);
    
    registerProperty(NamedProperties.allowStandupAssists);
  }

}

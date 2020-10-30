package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* This player's presence is very disturbing, whether it is caused by a
* massive cloud of flies, sprays of soporific musk, an aura of random
* chaos or intense cold, or a pheromone that causes fear and panic.
* Regardless of the nature of this mutation, any player must subtract 1
* from the D6 when they pass, intercept or catch for each opposing player
* with Disturbing Presence that is within three squares of them, even if the
* Disturbing Presence player is Prone or Stunned.
*/
public class DisturbingPresence extends Skill {

  public DisturbingPresence() {
    super("Disturbing Presence", SkillCategory.MUTATION);
  }

}

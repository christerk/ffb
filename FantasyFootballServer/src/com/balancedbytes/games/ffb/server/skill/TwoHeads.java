package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* Having two heads enables this player to watch where he is going and the
* opponent trying to make sure he does not get there at the same time.
* Add 1 to all Dodge rolls the player makes.
*/
public class TwoHeads extends ServerSkill {

  public TwoHeads() {
    super("Two Heads", SkillCategory.MUTATION);
  }

}
package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* Loners, through inexperience, arrogance, animal ferocity or just plain
* stupidity, do not work well with the rest of the team. As a result, a Loner
* may use team reRolls but has to roll a D6 first. On a roll of 4+, he may
* use the team re-roll as normal. On a roll of 1-3 the original result stands
* without being re-rolled but the team re-roll is lost (i.e. used).
*/
public class Loner extends ServerSkill {

  public Loner() {
    super("Loner", SkillCategory.EXTRAORDINARY);
  }

  @Override
  public int getCost(Player player) {
    return 0;
  }

}
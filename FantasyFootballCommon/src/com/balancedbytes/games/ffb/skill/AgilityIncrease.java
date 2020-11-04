package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

public class AgilityIncrease extends Skill {

  public AgilityIncrease() {
    super("+AG", SkillCategory.STAT_INCREASE);
  }

  @Override
  public int getCost(Player<?> player) {
    return 40000;
  }
}

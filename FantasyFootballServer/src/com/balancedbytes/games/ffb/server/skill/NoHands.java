package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player is unable to pick up, intercept or carry the ball and will fail any
* catch roll automatically, either because he literally has no hands or
* because his hands are full. If he attempts to pick up the ball then it will
* bounce, and will causes a turnover if it is his team's turn.
*/
public class NoHands extends ServerSkill {

  public NoHands() {
    super("No Hands", SkillCategory.EXTRAORDINARY);
  }

}

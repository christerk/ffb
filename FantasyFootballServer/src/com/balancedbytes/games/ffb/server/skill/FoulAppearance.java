package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* The player's appearance is so horrible that any opposing player that
* wants to block the player (or use a special attack that takes the place of
* a block) must first roll a D6 and score 2 or more. If the opposing player
* rolls a 1 he is too revolted to make the block and it is wasted (though the
* opposing team does not suffer a turnover).
*/
public class FoulAppearance extends ServerSkill {

  public FoulAppearance() {
    super("Foul Appearance", SkillCategory.MUTATION);
  }

}

package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player is specially trained in grappling techniques. This player may
* use Wrestle when he blocks or is blocked and a "Both Down" result on
* the Block dice is chosen by either coach. Instead of applying the 'Both
* Down' result, both players are wrestled to the ground. Both players are
* Placed Prone in their respective squares even if one or both have the
* Block skill. Do not make Armour rolls for either player. Use of this skill
* does not cause a turnover unless the active player was holding the ball.
*/
public class Wrestle extends Skill {

  public Wrestle() {
    super("Wrestle", SkillCategory.GENERAL);
  }

  @Override
  public String[] getSkillUseDescription() {
    return new String[] {
        "Using Wrestle will put down both you and your opponent.",
        "No Armor Roll is made. The ball carrier drops the ball."
    };
  }
}

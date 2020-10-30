package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* Immediately after declaring an Action with this player, roll a D6. On a 2 or
* more, the player may take his Action as normal. On a 1, the player 'takes
* root', and his MA is considered 0 until a drive ends, or he is Knocked
* Down or Placed Prone (and no, players from his own team may not try
* and block him in order to try to knock him down!). A player that has taken
* root may not Go For It, be pushed back for any reason, or use any skill
* that would allow him to move out of his current square or be Placed
* Prone. The player may block adjacent players without following-up as
* part of a Block Action however if a player fails his Take Root roll as part
* of a Blitz Action he may not block that turn (he can still roll to stand up if
* he is Prone).
*/
public class TakeRoot extends Skill {

  public TakeRoot() {
    super("Take Root", SkillCategory.EXTRAORDINARY);
  }

  @Override
  public String getConfusionMessage() {
    return "takes root";
  }
}

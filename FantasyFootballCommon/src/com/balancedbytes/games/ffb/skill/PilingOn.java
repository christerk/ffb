package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* The player may use this skill after he has made a block as part of one of
* his Block or Blitz Actions, but only if the Piling On player is currently
* standing adjacent to the victim and the victim was Knocked Down. You
* may re-roll the Armour roll or Injury roll for the victim. The Piling On
* player is Placed Prone in his own square - it is assumed that he rolls
* back there after flattening his opponent (do not make an Armour roll for
* him as he has been cushioned by the other player!). Piling On does not
* cause a turnover unless the Piling On player is carrying the ball. Piling
* On cannot be used with the Stab or Chainsaw skills.
*/
public class PilingOn extends Skill {

  public PilingOn() {
    super("Piling On", SkillCategory.STRENGTH);
  }

}

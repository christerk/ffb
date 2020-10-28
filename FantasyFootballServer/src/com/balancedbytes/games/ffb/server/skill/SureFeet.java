package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
*  The player may re-roll the D6 if he is Knocked Down when trying to Go
*  For It (see page 20). A player may only use the Sure Feet skill once per
*  turn.
*/
public class SureFeet extends ServerSkill {

  public SureFeet() {
    super("Sure Feet", SkillCategory.AGILITY);
  }

}

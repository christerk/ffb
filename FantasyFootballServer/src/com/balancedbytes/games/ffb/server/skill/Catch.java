package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player who has the Catch skill is allowed to re-roll the D6 if he fails a
* catch roll. It also allows the player to re-roll the D6 if he drops a hand-off
* or fails to make an interception.
*/
public class Catch extends ServerSkill {

  public Catch() {
    super("Catch", SkillCategory.AGILITY);
    // TODO Auto-generated constructor stub
  }

}
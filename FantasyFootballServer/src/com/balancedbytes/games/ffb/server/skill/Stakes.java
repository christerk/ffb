package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* This player is armed with special stakes that are blessed to cause extra
* damage to the Undead and those that work with them. This player may
* add 1 to the Armour roll when they make a Stab attack against any
* player playing for a Khemri, Necromantic, Undead or Vampire team.
*/
public class Stakes extends ServerSkill {

  public Stakes() {
    super("Stakes", SkillCategory.EXTRAORDINARY);
  }

}

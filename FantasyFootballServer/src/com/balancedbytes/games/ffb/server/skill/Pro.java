package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* A player with this skill is a hardened veteran. Such players are called
* professionals or Pros by other Blood Bowl players because they rarely
* make a mistake. Once per turn, a Pro is allowed to re-roll any one dice
* roll he has made other than Armour, Injury or Casualty, even if he is
* Prone or Stunned. However, before the re-roll may be made, his coach
* must roll a D6. On a roll of 4, 5 or 6 the re-roll may be made. On a roll of
* 1, 2 or 3 the original result stands and may not be re-rolled with a skill or
* team re-roll; however you can re-roll the Pro roll with a Team re-roll.
*/
public class Pro extends ServerSkill {

  public Pro() {
    super("Pro", SkillCategory.GENERAL);
  }

}

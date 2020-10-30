package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
* A player with the Leap skill is allowed to jump to any empty square within
* 2 squares even if it requires jumping over a player from either team.
* Making a leap costs the player two squares of movement. In order to
* make the leap, move the player to any empty square 1 to 2 squares from
* their current square and then make an Agility roll for the player. No
* modifiers apply to this D6 roll unless he has Very Long Legs. The player
* does not have to dodge to leave the square he starts in. If the player
* successfully makes the D6 roll then they make a perfect jump and may
* carry on moving. If the player fails the Agility roll then he is Knocked
* Down in the square that he was leaping to, and the opposing coach
* makes an Armour roll to see if he was injured. A failed leap causes a
* turnover, and the moving team's turn ends immediately. A player may
* only use the Leap skill once per Action.
*/
public class Leap extends Skill {

  public Leap() {
    super("Leap", SkillCategory.AGILITY);
  }

}

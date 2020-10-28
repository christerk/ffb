package com.balancedbytes.games.ffb.server.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.server.model.FanaticSkillProperty;
import com.balancedbytes.games.ffb.server.model.ServerSkill;

/**
* Players armed with a Ball & Chain can only take Move Actions. To move
* or Go For It, place the throw-in template over the player facing up or
* down the pitch or towards either sideline. Then roll a D6 and move the
* player one square in the indicated direction; no Dodge roll is required if
* you leave a tackle zone. If this movement takes the player off the pitch,
* they are beaten up by the crowd in the same manner as a player who
* has been pushed off the pitch. Repeat this process until the player runs
* out of normal movement (you may GFI using the same process if you
* wish). If during his Move Action he would move into an occupied square
* then the player will throw a block following normal blocking rules against
* whoever is in that square, friend or foe (and it even ignores Foul
* Appearance!). Prone or Stunned players in an occupied square are
* pushed back and an Armour roll is made to see if they are injured,
* instead of the block being thrown at them. The player must follow up if
* they push back another player, and will then carry on with their move as
* described above. If the player is ever Knocked Down or Placed Prone
* roll immediately for injury (no Armour roll is required). Stunned results for
* any Injury rolls are always treated as KO'd. A Ball & Chain player may
* use the Grab skill (as if a Block Action was being used) with his blocks (if
* he has learned it!). A Ball & Chain player may never use the Diving
* Tackle, Frenzy, Kick-Off Return, Pass Block or Shadowing skills.
*/
public class BallAndChain extends ServerSkill {

  public BallAndChain() {
    super("Ball and Chain", SkillCategory.EXTRAORDINARY);
    
    registerProperty(new FanaticSkillProperty());
  }

}

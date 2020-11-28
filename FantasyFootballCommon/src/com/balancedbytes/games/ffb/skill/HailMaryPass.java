package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* The player may throw the ball to any square on the playing pitch, no
* matter what the range: the range ruler is not used. Roll a D6. On a roll of
* 1 the player fumbles the throw, and the ball will bounce once from the
* thrower's square. On a roll of 2-6 the player may make the pass. The
* Hail Mary pass may not be intercepted, but it is never accurate - the ball
* automatically misses and scatters three squares. Note that if you are
* lucky, the ball will scatter back into the target square! This skill may not
* be used in a blizzard or with the Throw Team-Mate skill.  
*/
public class HailMaryPass extends Skill {

  public HailMaryPass() {
    super("Hail Mary Pass", SkillCategory.PASSING);
    
    registerProperty(NamedProperties.canPassToAnySquare);

  }

}

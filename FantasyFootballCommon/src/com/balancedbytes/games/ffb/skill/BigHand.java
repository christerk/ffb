package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PickupModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* One of the player's hands has grown monstrously large, yet remained
* completely functional. The player ignores modifier(s) for enemy tackle
* zones or Pouring Rain weather when he attempts to pick up the ball.
*/
public class BigHand extends Skill {

  public BigHand() {
    super("Big Hand", SkillCategory.MUTATION);
    
    registerProperty(NamedProperties.ignoreTacklezonesWhenPickingUp);
    registerProperty(NamedProperties.ignoreWeatherWhenPickingUp);
    registerModifier(PickupModifiers.BIG_HAND);
  }

}

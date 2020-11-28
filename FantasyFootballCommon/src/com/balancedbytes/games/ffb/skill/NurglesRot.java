package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* This player has a horrible infectious disease which spreads when he kills
* an opponent during a Block, Blitz or Foul Action. Instead of truly dying,
* the infected opponent becomes a new rookie Rotter. To do so, the
* opponent must have been removed from the roster during step 2.1 of the
* Post-game sequence, his Strength cannot exceed 4, and he cannot have
* the Decay, Regeneration or Stunty skills. The new Rotter can be added
* to the Nurgle team for free during step 5 of Updating Your Team Roster
* (see page 29) if the team has an open Roster slot. This new Rotter still
* counts at full value towards the total value of the Nurgle team.
*/
public class NurglesRot extends Skill {

  public NurglesRot() {
    super("Nurgle's Rot", SkillCategory.EXTRAORDINARY);
    
    registerProperty(NamedProperties.hasNurglesRot);

  }

}

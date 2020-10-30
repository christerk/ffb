package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PassingModifiers;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The player is so small that they are very difficult to tackle because they
 * can duck underneath opposing players' outstretched arms and run
 * between their legs. On the other hand, Stunty players are just a bit too
 * small to throw the ball very well, and are easily injured. To represent
 * these things a player with the Stunty skill may ignore any enemy tackle
 * zones on the square he is moving to when he makes a Dodge roll (i.e.,
 * they always end up with a +1 Dodge roll modifier), but must subtract 1
 * from the roll when they pass. In addition, this player treats a roll of 7 and
 * 9 on the Injurytable after any modifiers have been applied as a KO'd and
 * Badly Hurt result respectively rather than the normal results. Stunties
 * that are armed with a Secret Weapon are not allowed to ignore enemy
 * tackle zones, but still suffer the other penalties.
 */
public class Stunty extends Skill {

  public Stunty() {
    super("Stunty", SkillCategory.EXTRAORDINARY);
    
    registerModifier(PassingModifiers.STUNTY);
  }

}

package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
* A player with this skill is armed with something very good at stabbing,
* slashing or hacking up an opponent, like sharp fangs or a trusty dagger.
* This player may attack an opponent with their stabbing attack instead of
* throwing a block at them. Make an unmodified Armour roll for the victim.
* If the score is less than or equal to the victim's Armour value then the
* attack has no effect. If the score beats the victim's Armour value then
* they have been wounded and an unmodified Injury roll must be made. If
* Stab is used as part of a Blitz Action, the player cannot continue moving
* after using it. Casualties caused by a stabbing attack do not count for
* Star Player points.
*/
public class Stab extends Skill {

  public Stab() {
    super("Stab", SkillCategory.EXTRAORDINARY);

    registerProperty(NamedProperties.canPerformArmourRollInsteadOfBlock);
  }

}

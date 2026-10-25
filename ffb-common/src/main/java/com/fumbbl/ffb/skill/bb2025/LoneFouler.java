package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * When this player performs a Foul Action, this player may re-roll the Armour Roll.
 * No Offensive or Defensive Assists are applied to the re-rolled Armour Roll.
 */

@RulesCollection(RulesCollection.Rules.BB2025)
public class LoneFouler extends Skill {

  public LoneFouler() {
    super("Lone Fouler", SkillCategory.DEVIOUS, SkillUsageType.ONCE_PER_TURN);
  }
  
}

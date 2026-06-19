package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when Josef’s armour is broken as the result of an Armour Roll,
 * you may choose to have the Armour Roll re-rolled.
 */
@RulesCollection(Rules.BB2025)
public class DwarfenGrit extends Skill {
  public DwarfenGrit() {
    super("Dwarfen Grit", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
  }
}

package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when an opposition player is Knocked Down as a result
 * of a Block Action performed by Varag, he may re-roll the Armour Roll.
 */
@RulesCollection(Rules.BB2025)
public class KrumpAndSmash extends Skill {
  public KrumpAndSmash() {
    super("Krump and Smash", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
  }
}

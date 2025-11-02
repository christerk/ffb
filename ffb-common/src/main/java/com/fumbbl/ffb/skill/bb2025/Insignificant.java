package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When creating a Team Draft List, you may not include more players with this Trait than players without this Trait.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class Insignificant extends Skill {

  public Insignificant() {
    super("Insignificant", SkillCategory.TRAIT);
  }
  
}

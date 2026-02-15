package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class UnstoppableMomentum extends Skill {
  public UnstoppableMomentum() {
    super("Unstoppable Momentum", SkillCategory.TRAIT);
  }

  @Override
  public void postConstruct() {
    registerProperty(NamedProperties.canRerollSingleBlockDieDuringBlitz);
    registerRerollSource(ReRolledActions.SINGLE_BLOCK_DIE, ReRollSources.UNSTOPPABLE_MOMENTUM);
  }
}

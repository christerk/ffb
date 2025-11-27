package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.LoneFouler;
import com.fumbbl.ffb.server.injury.modification.bb2025.LoneFoulerModification;

@RulesCollection(RulesCollection.Rules.BB2025)
public class LoneFoulerBehaviour extends SkillBehaviour<LoneFouler> {

  public LoneFoulerBehaviour() {
    super();
    registerModifier(new LoneFoulerModification());
  }
}

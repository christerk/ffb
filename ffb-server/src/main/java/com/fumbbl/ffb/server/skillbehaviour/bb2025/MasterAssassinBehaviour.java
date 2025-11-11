package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.MasterAssassinModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.special.MasterAssassin;

@RulesCollection(RulesCollection.Rules.BB2025)
public class MasterAssassinBehaviour extends SkillBehaviour<MasterAssassin> {

  public MasterAssassinBehaviour() {
    super();
    registerModifier(new MasterAssassinModification());
  }
}

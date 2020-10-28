package com.balancedbytes.games.ffb.server.model.modifier;

import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.model.ISkillProperty;

public class CancelSkillProperty implements ISkillProperty {
  private Skill cancelledSkill;
  public CancelSkillProperty(Skill cancelledSkill) {
    this.cancelledSkill = cancelledSkill;
  }

  public boolean cancelsSkill(Skill skill) {
    return skill.equals(cancelledSkill);
  }
}

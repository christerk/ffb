package com.balancedbytes.games.ffb.server.model;

import com.balancedbytes.games.ffb.model.Skill;

public class CancelSkillProperty implements ISkillProperty {
  private Skill cancelledSkill;
  public CancelSkillProperty(Skill cancelledSkill) {
    this.cancelledSkill = cancelledSkill;
  }

  public boolean cancelsSkill(Skill skill) {
    return skill.equals(cancelledSkill);
  }
}

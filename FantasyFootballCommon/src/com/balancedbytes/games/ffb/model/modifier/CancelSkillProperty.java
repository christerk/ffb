package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.model.ISkillProperty;
import com.balancedbytes.games.ffb.model.Skill;

public class CancelSkillProperty implements ISkillProperty {
  private Skill cancelledSkill;
  public CancelSkillProperty(Skill cancelledSkill) {
    this.cancelledSkill = cancelledSkill;
  }

  public boolean cancelsSkill(Skill skill) {
    return skill.equals(cancelledSkill);
  }

  @Override
  public boolean matches(ISkillProperty other) {
    return other instanceof CancelSkillProperty && ((CancelSkillProperty)other).cancelledSkill.equals(cancelledSkill);
  }
}

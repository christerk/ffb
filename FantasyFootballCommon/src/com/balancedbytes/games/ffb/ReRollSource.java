package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;

/**
 * 
 * @author Kalimar
 */
public enum ReRollSource implements INamedObject {
  
  TEAM_RE_ROLL("Team ReRoll"),
  DODGE(SkillConstants.DODGE),
  PRO(SkillConstants.PRO),
  SURE_FEET(SkillConstants.SURE_FEET),
  SURE_HANDS(SkillConstants.SURE_HANDS),
  CATCH(SkillConstants.CATCH),
  PASS(SkillConstants.PASS),
  WINNINGS("Winnings"),
  LONER(SkillConstants.LONER),
  LEADER(SkillConstants.LEADER),
  MONSTROUS_MOUTH(SkillConstants.MONSTROUS_MOUTH);

  private int fId;
  private String fName;
  private Skill fSkill;
  
  private ReRollSource(String pName) {
    fName = pName;
  }
  
  private ReRollSource(Skill pSkill) {
    this(pSkill.getName());
    fSkill = pSkill;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public Skill getSkill() {
    return fSkill;
  }
  
}

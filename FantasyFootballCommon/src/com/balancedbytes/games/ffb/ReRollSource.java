package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum ReRollSource implements IEnumWithName {
  
  TEAM_RE_ROLL("Team ReRoll"),
  DODGE(Skill.DODGE),
  PRO(Skill.PRO),
  SURE_FEET(Skill.SURE_FEET),
  SURE_HANDS(Skill.SURE_HANDS),
  CATCH(Skill.CATCH),
  PASS(Skill.PASS),
  WINNINGS("Winnings"),
  LONER(Skill.LONER),
  LEADER(Skill.LEADER),
  MONSTROUS_MOUTH(Skill.MOUNSTROUS_MOUTH);

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

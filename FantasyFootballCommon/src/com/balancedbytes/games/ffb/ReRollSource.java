package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum ReRollSource implements IEnumWithId, IEnumWithName {
  
  TEAM_RE_ROLL(1, "Team ReRoll"),
  DODGE(2, Skill.DODGE),
  PRO(3, Skill.PRO),
  SURE_FEET(4, Skill.SURE_FEET),
  SURE_HANDS(5, Skill.SURE_HANDS),
  CATCH(6, Skill.CATCH),
  PASS(7, Skill.PASS),
  WINNINGS(8, "Winnings"),
  LONER(9, Skill.LONER),
  LEADER(10, Skill.LEADER);

  private int fId;
  private String fName;
  private Skill fSkill;
  
  private ReRollSource(int pValue, String pName) {
    fId = pValue;
    fName = pName;
  }
  
  private ReRollSource(int pValue, Skill pSkill) {
    this(pValue, pSkill.getName());
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

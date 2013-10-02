package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class SkillFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public Skill forName(String pName) {
    for (Skill skill : Skill.values()) {
      if (skill.getName().equalsIgnoreCase(pName)) {
        return skill;
      }
    }
    if ("Ball & Chain".equalsIgnoreCase(pName) || "Ball &amp; Chain".equalsIgnoreCase(pName)) {
      return Skill.BALL_AND_CHAIN;
    }
    return null;
  }

  public Skill forId(int pId) {
    if (pId > 0) {
      for (Skill skill : Skill.values()) {
        if (pId == skill.getId()) {
          return skill;
        }
      }
    }
    return null;
  }

}

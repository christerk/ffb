package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class SkillUseFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public SkillUse forId(int pId) {
    if (pId > 0) {
      for (SkillUse skillUse : SkillUse.values()) {
        if (pId == skillUse.getId()) {
          return skillUse;
        }
      }
    }
    return null;
  }
  
  public SkillUse forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (SkillUse skillUse : SkillUse.values()) {
        if (pName.equalsIgnoreCase(skillUse.getName())) {
          return skillUse;
        }
      }
    }
    return null;
  }

}

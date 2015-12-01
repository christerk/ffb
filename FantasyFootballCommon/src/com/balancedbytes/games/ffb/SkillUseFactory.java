package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class SkillUseFactory implements IEnumWithNameFactory {

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

package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class PlayerGenderFactory implements IEnumWithNameFactory {
  
  public PlayerGender forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (PlayerGender gender : PlayerGender.values()) {
        if (pName.equalsIgnoreCase(gender.getName())) {
          return gender;
        }
      }
    }
    return null;
  }

  public PlayerGender forTypeString(String pTypeString) {
    if (StringTool.isProvided(pTypeString)) {
      for (PlayerGender gender : PlayerGender.values()) {
        if (pTypeString.equalsIgnoreCase(gender.getTypeString())) {
          return gender;
        }
      }
    }
    return null;
  }

}

package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class AnimationTypeFactory implements IEnumWithNameFactory {
  
  public AnimationType forName(String pName) {
    for (AnimationType type : AnimationType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}

package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class AnimationTypeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public AnimationType forName(String pName) {
    for (AnimationType type : AnimationType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

  public AnimationType forId(int pId) {
    for (AnimationType type : AnimationType.values()) {
      if (type.getId() == pId) {
        return type;
      }
    }
    return null;
  }

}

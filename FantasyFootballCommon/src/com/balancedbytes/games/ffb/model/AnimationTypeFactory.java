package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class AnimationTypeFactory implements INamedObjectFactory {
  
  public AnimationType forName(String pName) {
    for (AnimationType type : AnimationType.values()) {
      if (type.getName().equalsIgnoreCase(pName)) {
        return type;
      }
    }
    return null;
  }

}

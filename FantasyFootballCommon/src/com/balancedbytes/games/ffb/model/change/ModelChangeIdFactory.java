package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeIdFactory implements IEnumWithNameFactory {
  
  public ModelChangeId forName(String pName) {
    for (ModelChangeId changeId : ModelChangeId.values()) {
      if (changeId.getName().equalsIgnoreCase(pName)) {
        return changeId;
      }
    }
    return null;
  }

}

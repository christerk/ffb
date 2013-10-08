package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ModelChangeId forName(String pName) {
    for (ModelChangeId changeId : ModelChangeId.values()) {
      if (changeId.getName().equalsIgnoreCase(pName)) {
        return changeId;
      }
    }
    return null;
  }

  public ModelChangeId forId(int pId) {
    for (ModelChangeId changeId : ModelChangeId.values()) {
      if (changeId.getId() == pId) {
        return changeId;
      }
    }
    return null;
  }

}

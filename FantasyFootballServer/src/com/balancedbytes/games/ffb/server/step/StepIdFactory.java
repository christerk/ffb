package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class StepIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public StepId forId(int pId) {
    for (StepId stepId : StepId.values()) {
      if (stepId.getId() == pId) {
        return stepId;
      }
    }
    return null;
  }

  public StepId forName(String pName) {
    for (StepId stepId : StepId.values()) {
      if (stepId.getName().equalsIgnoreCase(pName)) {
        return stepId;
      }
    }
    return null;
  }
  
}

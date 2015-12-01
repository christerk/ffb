package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class StepIdFactory implements IEnumWithNameFactory {
  
  public StepId forName(String pName) {
    for (StepId stepId : StepId.values()) {
      if (stepId.getName().equalsIgnoreCase(pName)) {
        return stepId;
      }
    }
    return null;
  }
  
}

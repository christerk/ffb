package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class StepActionFactory implements IEnumWithNameFactory {
  
  public StepAction forName(String pName) {
    for (StepAction stepAction : StepAction.values()) {
      if (stepAction.getName().equalsIgnoreCase(pName)) {
        return stepAction;
      }
    }
    return null;
  }

}

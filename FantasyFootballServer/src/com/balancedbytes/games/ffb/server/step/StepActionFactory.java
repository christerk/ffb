package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class StepActionFactory implements INamedObjectFactory {
  
  public StepAction forName(String pName) {
    for (StepAction stepAction : StepAction.values()) {
      if (stepAction.getName().equalsIgnoreCase(pName)) {
        return stepAction;
      }
    }
    return null;
  }

}

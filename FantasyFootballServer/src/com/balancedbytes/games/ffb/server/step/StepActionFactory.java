package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class StepActionFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public StepAction forId(int pId) {
    for (StepAction stepAction : StepAction.values()) {
      if (stepAction.getId() == pId) {
        return stepAction;
      }
    }
    return null;
  }

  public StepAction forName(String pName) {
    for (StepAction stepAction : StepAction.values()) {
      if (stepAction.getName().equalsIgnoreCase(pName)) {
        return stepAction;
      }
    }
    return null;
  }

}

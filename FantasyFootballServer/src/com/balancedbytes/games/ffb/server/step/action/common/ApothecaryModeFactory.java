package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ApothecaryModeFactory implements IEnumWithNameFactory {
  
  public ApothecaryMode forName(String pName) {
    for (ApothecaryMode mode : ApothecaryMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}

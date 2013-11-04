package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ApothecaryModeFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ApothecaryMode forName(String pName) {
    for (ApothecaryMode mode : ApothecaryMode.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

  public ApothecaryMode forId(int pId) {
    if (pId > 0) {
      for (ApothecaryMode stepLabel : ApothecaryMode.values()) {
        if (stepLabel.getId() == pId) {
          return stepLabel;
        }
      }
    }
    return null;
  }

}

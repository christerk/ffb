package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ApothecaryStatusFactory implements IEnumWithNameFactory {
  
  public ApothecaryStatus forName(String pName) {
    for (ApothecaryStatus status : ApothecaryStatus.values()) {
      if (status.getName().equalsIgnoreCase(pName)) {
        return status;
      }
    }
    return null;
  }

}

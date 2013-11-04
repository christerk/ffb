package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ApothecaryStatusFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ApothecaryStatus forName(String pName) {
    for (ApothecaryStatus status : ApothecaryStatus.values()) {
      if (status.getName().equalsIgnoreCase(pName)) {
        return status;
      }
    }
    return null;
  }

  public ApothecaryStatus forId(int pId) {
    if (pId > 0) {
      for (ApothecaryStatus status : ApothecaryStatus.values()) {
        if (status.getId() == pId) {
          return status;
        }
      }
    }
    return null;
  }

}

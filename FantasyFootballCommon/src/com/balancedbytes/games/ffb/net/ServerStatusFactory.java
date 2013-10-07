package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ServerStatusFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ServerStatus forName(String pName) {
    for (ServerStatus serverStatus : ServerStatus.values()) {
      if (serverStatus.getName().equalsIgnoreCase(pName)) {
        return serverStatus;
      }
    }
    return null;
  }

  public ServerStatus forId(int pId) {
    if (pId > 0) {
      for (ServerStatus serverStatus : ServerStatus.values()) {
        if (pId == serverStatus.getId()) {
          return serverStatus;
        }
      }
    }
    return null;
  }

}

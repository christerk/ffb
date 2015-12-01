package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ServerStatusFactory implements IEnumWithNameFactory {
  
  public ServerStatus forName(String pName) {
    for (ServerStatus serverStatus : ServerStatus.values()) {
      if (serverStatus.getName().equalsIgnoreCase(pName)) {
        return serverStatus;
      }
    }
    return null;
  }

}

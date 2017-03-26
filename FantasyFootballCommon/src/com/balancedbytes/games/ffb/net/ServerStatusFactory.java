package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class ServerStatusFactory implements INamedObjectFactory {
  
  public ServerStatus forName(String pName) {
    for (ServerStatus serverStatus : ServerStatus.values()) {
      if (serverStatus.getName().equalsIgnoreCase(pName)) {
        return serverStatus;
      }
    }
    return null;
  }

}

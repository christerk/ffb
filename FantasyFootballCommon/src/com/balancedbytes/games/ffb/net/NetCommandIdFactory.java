package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class NetCommandIdFactory implements IEnumWithNameFactory {
  
  public NetCommandId forName(String pName) {
    for (NetCommandId commandId : NetCommandId.values()) {
      if (commandId.getName().equalsIgnoreCase(pName)) {
        return commandId;
      }
    }
    return null;
  }

}

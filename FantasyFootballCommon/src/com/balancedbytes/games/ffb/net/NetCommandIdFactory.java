package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class NetCommandIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public NetCommandId forName(String pName) {
    for (NetCommandId commandId : NetCommandId.values()) {
      if (commandId.getName().equalsIgnoreCase(pName)) {
        return commandId;
      }
    }
    return null;
  }

  public NetCommandId forId(int pId) {
    for (NetCommandId commandId : NetCommandId.values()) {
      if (commandId.getId() == pId) {
        return commandId;
      }
    }
    return null;
  }
  
}

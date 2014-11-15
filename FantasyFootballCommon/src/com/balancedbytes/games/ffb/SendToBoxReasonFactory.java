package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class SendToBoxReasonFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public SendToBoxReason forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (SendToBoxReason reason : SendToBoxReason.values()) {
        if (pName.equalsIgnoreCase(reason.getName())) {
          return reason;
        }
      }
      // backward compatibility (name change)
      if ("wrestle".equals(pName)) {
        return SendToBoxReason.BALL_AND_CHAIN;
      }
    }
    return null;
  }

  public SendToBoxReason forId(int pId) {
    for (SendToBoxReason reason : SendToBoxReason.values()) {
      if (reason.getId() == pId) {
        return reason;
      }
    }
    return null;
  }

}

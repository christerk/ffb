package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ReportIdFactory implements IEnumWithNameFactory {

  public ReportId forName(String pName) {
    for (ReportId mode : ReportId.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}

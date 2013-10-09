package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class ReportIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public ReportId forId(int pId) {
    if (pId > 0) {
      for (ReportId mode : ReportId.values()) {
        if (mode.getId() == pId) {
          return mode;
        }
      }
    }
    return null;
  }

  public ReportId forName(String pName) {
    for (ReportId mode : ReportId.values()) {
      if (mode.getName().equalsIgnoreCase(pName)) {
        return mode;
      }
    }
    return null;
  }

}

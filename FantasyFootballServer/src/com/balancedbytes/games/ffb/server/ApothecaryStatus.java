package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;


public enum ApothecaryStatus implements IEnumWithId, IEnumWithName {
  
  NO_APOTHECARY(1, "noApothecary"),
  DO_REQUEST(2, "doRequest"),
  WAIT_FOR_APOTHECARY_USE(3, "waitForApothecaryUse"),
  USE_APOTHECARY(4, "useApothecary"),
  DO_NOT_USE_APOTHECARY(5, "doNotUseApothecary"),
  RESULT_CHOICE(6, "resultChoice"),
  WAIT_FOR_IGOR_USE(7, "waitForIgorUse"),
  USE_IGOR(8, "useIgor"),
  DO_NOT_USE_IGOR(9, "doNotUseIgor");
  
  private int fId;
  private String fName;
  
  private ApothecaryStatus(int pId, String pName) {
    fId = pId;
    fName = pName;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }

}

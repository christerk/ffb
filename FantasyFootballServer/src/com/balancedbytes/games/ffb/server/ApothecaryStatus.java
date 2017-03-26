package com.balancedbytes.games.ffb.server;

import com.balancedbytes.games.ffb.INamedObject;


public enum ApothecaryStatus implements INamedObject {
  
  NO_APOTHECARY("noApothecary"),
  DO_REQUEST("doRequest"),
  WAIT_FOR_APOTHECARY_USE("waitForApothecaryUse"),
  USE_APOTHECARY("useApothecary"),
  DO_NOT_USE_APOTHECARY("doNotUseApothecary"),
  RESULT_CHOICE("resultChoice"),
  WAIT_FOR_IGOR_USE("waitForIgorUse"),
  USE_IGOR("useIgor"),
  DO_NOT_USE_IGOR("doNotUseIgor");
  
  private String fName;
  
  private ApothecaryStatus(String pName) {
    fName = pName;
  }
  
  public String getName() {
    return fName;
  }

}

package com.balancedbytes.games.ffb.server;


public enum ApothecaryStatus {
  
  NO_APOTHECARY(1),
  DO_REQUEST(2),
  WAIT_FOR_APOTHECARY_USE(3),
  USE_APOTHECARY(4),
  DO_NOT_USE_APOTHECARY(5),
  RESULT_CHOICE(6),
  WAIT_FOR_IGOR_USE(7),
  USE_IGOR(8),
  DO_NOT_USE_IGOR(9);
  
  private int fId;
  
  private ApothecaryStatus(int pId) {
    fId = pId;
  }
  
  public int getId() {
    return fId;
  }
  
  public static ApothecaryStatus fromId(int pId) {
    for (ApothecaryStatus status : values()) {
      if (status.getId() == pId) {
        return status;
      }
    }
    return null;
  }

}

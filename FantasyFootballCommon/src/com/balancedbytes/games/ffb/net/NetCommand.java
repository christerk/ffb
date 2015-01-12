package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public abstract class NetCommand implements IJsonSerializable {
  
  private int fSize;
  
  public abstract NetCommandId getId();
  
  public int size() {
    return fSize;
  }
  
  public void setSize(int pSize) {
    fSize = pSize;
  }
  
  public boolean isInternal() {
    return false;
  }
  
}

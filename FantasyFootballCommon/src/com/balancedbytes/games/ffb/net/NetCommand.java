package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public abstract class NetCommand implements IByteArrayReadable, IJsonSerializable {
  
  private int fSize;
  
  public abstract NetCommandId getId();
  
  public void initFrom(byte[] pBytes) {
    ByteArray byteArray = new ByteArray(pBytes);
    if (getId().getId() != byteArray.getSmallInt()) {
      throw new FantasyFootballException("Wrong NetCommand Id.");
    }
    int size = byteArray.getSmallInt();
    setSize(size);
    if (pBytes.length < size) {
      throw new FantasyFootballException("Not enough bytes to convert into NetCommand.");
    }
    initFrom(byteArray);
  }

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

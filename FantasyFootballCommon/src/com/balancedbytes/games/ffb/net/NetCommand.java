package com.balancedbytes.games.ffb.net;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public abstract class NetCommand implements IByteArraySerializable, IJsonSerializable {
  
  private int fSize;
  
  public abstract NetCommandId getId();
  
  public byte[] toBytes() {
    ByteList byteList = new ByteList();
    byteList.addSmallInt(getId().getId());
    byteList.addSmallInt(0);  // placeholder for size
    addTo(byteList);
    byte[] bytes = byteList.toBytes();
    int size = bytes.length;
    setSize(size);
    byte[] sizeBytes = ByteList.convertSmallIntToByteArray(size);
    bytes[2] = sizeBytes[0];
    bytes[3] = sizeBytes[1];
    return bytes;
  }
  
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
    if (fSize == 0) {
      toBytes();
    }
    return fSize;
  }
  
  public void setSize(int pSize) {
    fSize = pSize;
  }
  
  public boolean isInternal() {
    return false;
  }
  
}

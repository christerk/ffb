package com.balancedbytes.games.ffb.bytearray;

import com.balancedbytes.games.ffb.json.IJsonSerializable;




/**
 * 
 * @author Kalimar
 */
public interface IByteArraySerializable extends IJsonSerializable {
  
  // TODO: extends IJsonSerializable to show where the new interface is missing
  // subclasses should implement the interface themselves
  // this will be deleted after refactoring
  
  public int getByteArraySerializationVersion();
  
  public void addTo(ByteList pByteList);
  
  public int initFrom(ByteArray pByteArray);

}

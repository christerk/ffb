package com.balancedbytes.games.ffb.bytearray;


/**
 * 
 * @author Kalimar
 */
public interface IByteArraySerializable {
  
  // TODO: extends IJsonSerializable to show where the new interface is missing
  // subclasses should implement the interface themselves
  // this will be deleted after refactoring
  
  public int getByteArraySerializationVersion();
  
  public void addTo(ByteList pByteList);
  
  public int initFrom(ByteArray pByteArray);

}

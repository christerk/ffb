package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;

/**
 * 
 * @author Kalimar
 */
public interface IModelChange extends IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "modelChange";
  
  public static final String XML_ATTRIBUTE_ID = "id";
  
  public ModelChangeId getId();
  
  public void applyTo(Game pGame);
  
  public IModelChange transform();

}

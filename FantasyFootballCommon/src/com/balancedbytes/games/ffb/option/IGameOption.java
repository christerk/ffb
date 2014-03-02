package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;


/**
 * 
 * @author Kalimar
 */
public interface IGameOption extends IJsonSerializable, IXmlSerializable {
  
  public static final String XML_TAG = "option";
  public static final String XML_ATTRIBUTE_NAME = "name";
  public static final String XML_ATTRIBUTE_VALUE = "value";

  public GameOptionId getId();
  
  public String getValueAsString();
  
  public IGameOption setValue(String pValue);
  
  public boolean isChanged();
  
  public String getDisplayMessage();
  
  public IGameOption reset();
  
}

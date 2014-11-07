package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.json.IJsonWriteable;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;


/**
 * 
 * @author Kalimar
 */
public interface IGameOption extends IJsonWriteable, IXmlWriteable {
  
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

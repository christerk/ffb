package com.balancedbytes.games.ffb.xml;

import org.xml.sax.Attributes;

/**
 * 
 * @author Kalimar
 */
public interface IXmlReadable {
	
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes);
  
  public boolean endXmlElement(String pXmlTag, String pValue);

}

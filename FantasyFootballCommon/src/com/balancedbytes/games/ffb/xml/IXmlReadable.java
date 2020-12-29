package com.balancedbytes.games.ffb.xml;

import org.xml.sax.Attributes;

import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public interface IXmlReadable {

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes);

	public boolean endXmlElement(Game game, String pXmlTag, String pValue);

}

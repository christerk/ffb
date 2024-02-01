package com.fumbbl.ffb.xml;

import javax.xml.transform.sax.TransformerHandler;

/**
 * 
 * @author Kalimar
 */
public interface IXmlWriteable {

	public void addToXml(TransformerHandler pHandler);

	public String toXml(boolean pIndent);

}

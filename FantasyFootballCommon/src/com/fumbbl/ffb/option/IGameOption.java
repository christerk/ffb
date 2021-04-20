package com.fumbbl.ffb.option;

import com.fumbbl.ffb.json.IJsonWriteable;
import com.fumbbl.ffb.xml.IXmlWriteable;

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

}

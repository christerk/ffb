package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class GameOptionValue implements IXmlSerializable, IByteArraySerializable, IJsonSerializable {

  public static final String XML_TAG = "option";
  
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_VALUE = "value";

	private GameOption fGameOption;
	private int fValue;
	
	public GameOptionValue() {
		super();
	}
	
	public GameOptionValue(GameOption pName, int pValue) {
		setOption(pName);
		setValue(pValue);
	}
	
	public GameOption getOption() {
		return fGameOption;
	}
	
	public void setOption(GameOption pOption) {
		fGameOption = pOption;
	}
	
	public int getValue() {
		return fValue;
	}
	
	public void setValue(int pValue) {
		fValue = pValue;
	}
	
	public boolean isEnabled() {
		return (getValue() != 0);
	}
	
	public boolean isChanged() {
		return (getValue() != getOption().getDefaultValue());
	}
	
  public String getGroup() {
		return getOption().getGroup();
	}
	
	public String getChangedMessage() {
		return StringTool.bind(getOption().getChangedMessage(), StringTool.formatThousands(getValue()));
	}
	
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, (getOption() != null) ? getOption().getName() : null);
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
  	if (XML_TAG.equals(pXmlTag)) {
  	  String name = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME);
  		setOption(new GameOptionFactory().forName(name));
  		String value = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
  		if ("true".equalsIgnoreCase(value)) {
  			setValue(1);
  		} else if ("false".equalsIgnoreCase(value)) {
  			setValue(0);
  		} else {
  			setValue(Integer.parseInt(value));
  		}
  	}
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString((getOption() != null) ? getOption().getName() : null);
    pByteList.addInt(getValue());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setOption(new GameOptionFactory().forName(pByteArray.getString()));
    setValue(pByteArray.getInt());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.GAME_OPTION.addTo(jsonObject, fGameOption);
    IJsonOption.VALUE.addTo(jsonObject, fValue);
    return jsonObject;
  }
  
  public GameOptionValue initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGameOption = (GameOption) IJsonOption.GAME_OPTION.getFrom(jsonObject);
    fValue = IJsonOption.VALUE.getFrom(jsonObject);
    return this;
  }
	
}

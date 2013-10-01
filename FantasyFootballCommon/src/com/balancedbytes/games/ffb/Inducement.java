package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Inducement implements IByteArraySerializable, IXmlSerializable {
  
  public static final String XML_TAG = "inducement";
  
  private static final String _XML_ATTRIBUTE_TYPE = "type";
  private static final String _XML_ATTRIBUTE_VALUE = "value";
  private static final String _XML_ATTRIBUTE_USES = "uses";

  private InducementType fType;
  private int fValue;
  private int fUses;

  public Inducement() {
    super();
  }
  
  public Inducement(InducementType pType, int pValue) {
    fType = pType;
    setValue(pValue);
  }

  public InducementType getType() {
    return fType;
  }
  
  public int getValue() {
    return fValue;
  }
  
  public void setValue(int pValue) {
    fValue = pValue;
  }
  
  public int getUses() {
    return fUses;
  }
  
  public void setUses(int pCurrent) {
    fUses = pCurrent;
  }
  
  public int getUsesLeft() {
    return Math.max(0, getValue() - getUses());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, (getType() != null) ? getType().getName() : null);
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getValue());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USES, getUses());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      fType = new InducementTypeFactory().forName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_TYPE));
      fValue = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
      fUses = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_USES);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getType() != null) ? getType().getId() : 0)); 
    pByteList.addByte((byte) getValue()); 
    pByteList.addByte((byte) getUses());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fType = new InducementTypeFactory().forId(pByteArray.getByte());
    fValue = pByteArray.getByte();
    fUses = pByteArray.getByte();
    if (byteArraySerializationVersion < 2) {
    	pByteArray.getByte();  // modifier is deprecated
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fType);
    IJsonOption.VALUE.addTo(jsonObject, fValue);
    IJsonOption.USES.addTo(jsonObject, fUses);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(jsonObject);
    fValue = IJsonOption.VALUE.getFrom(jsonObject);
    fUses = IJsonOption.USES.getFrom(jsonObject);
  }
  
}

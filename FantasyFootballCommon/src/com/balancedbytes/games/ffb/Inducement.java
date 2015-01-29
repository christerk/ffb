package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
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
public class Inducement implements IXmlSerializable, IJsonSerializable {
  
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
    String typeName = (fType != null) ? fType.getName() : null;
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, typeName);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, fValue);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USES, fUses);
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    InducementTypeFactory typeFactory = new InducementTypeFactory();
    if (XML_TAG.equals(pXmlTag)) {
      String typeName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_TYPE);
      fType = typeFactory.forName(typeName);
      fValue = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
      fUses = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_USES);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fType);
    IJsonOption.VALUE.addTo(jsonObject, fValue);
    IJsonOption.USES.addTo(jsonObject, fUses);
    return jsonObject;
  }
  
  public Inducement initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(jsonObject);
    fValue = IJsonOption.VALUE.getFrom(jsonObject);
    fUses = IJsonOption.USES.getFrom(jsonObject);
    return this;
  }
  
}

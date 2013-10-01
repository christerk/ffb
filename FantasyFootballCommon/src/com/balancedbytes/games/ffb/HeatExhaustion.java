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
public class HeatExhaustion implements IByteArraySerializable, IXmlSerializable {
  
  public static final String XML_TAG = "heatExhaustion";
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_EXHAUSTED = "exhausted";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  
  private String fPlayerId;
  private boolean fExhausted;
  private int fRoll;
  
  public HeatExhaustion() {
    super();
  }

  public HeatExhaustion(String pPlayerId, boolean pExhausted, int pRoll) {
    fPlayerId = pPlayerId;
    fExhausted = pExhausted;
    fRoll = pRoll;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isExhausted() {
    return fExhausted;
  }

  public int getRoll() {
    return fRoll;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_EXHAUSTED, isExhausted());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      fPlayerId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_PLAYER_ID);
      fExhausted = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_EXHAUSTED);
      fRoll = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_ROLL);
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
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isExhausted());
    pByteList.addByte((byte) getRoll());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fExhausted = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.EXHAUSTED.addTo(jsonObject, fExhausted);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fExhausted = IJsonOption.EXHAUSTED.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
  }

  
}

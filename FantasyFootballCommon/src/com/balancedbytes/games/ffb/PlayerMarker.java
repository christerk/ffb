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
public class PlayerMarker implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "playerMarker";
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_HOME_TEXT = "homeText";
  private static final String _XML_ATTRIBUTE_AWAY_TEXT = "awayText";  

  private String fPlayerId;
  private String fHomeText;
  private String fAwayText;
  
  public PlayerMarker() {
    super();
  }
  
  public PlayerMarker(String pPlayerId) {
    fPlayerId = pPlayerId;
  }

  public String getPlayerId() {
    return fPlayerId;
  }  
  
  public void setHomeText(String pHomeText) {
    fHomeText = pHomeText;
  }
  
  public String getHomeText() {
    return fHomeText;
  }
  
  public void setAwayText(String pAwayText) {
    fAwayText = pAwayText;
  }
  
  public String getAwayText() {
    return fAwayText;
  }
  
  public int hashCode() {
    return getPlayerId().hashCode();
  }
  
  public boolean equals(Object pObj) {
    return ((pObj instanceof PlayerMarker) && getPlayerId().equals(((PlayerMarker) pObj).getPlayerId()));
  }
 
  // Transformation
  
  public PlayerMarker transform() {
    PlayerMarker transformedMarker = new PlayerMarker(getPlayerId());
    transformedMarker.setAwayText(getHomeText());
    transformedMarker.setHomeText(getAwayText());
    return transformedMarker;
  }
  
  public static PlayerMarker transform(PlayerMarker pFieldMarker) {
    return (pFieldMarker != null) ? pFieldMarker.transform() : null;
  }
   
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_HOME_TEXT, getHomeText());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AWAY_TEXT, getAwayText());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlReadable xmlElement = this;
    if (XML_TAG.equals(pXmlTag)) {
      fPlayerId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_PLAYER_ID);
      fHomeText = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_HOME_TEXT);
      fAwayText = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_AWAY_TEXT);
    }
    return xmlElement;
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
    pByteList.addString(getHomeText());
    pByteList.addString(getAwayText());
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fHomeText = pByteArray.getString();
    fAwayText = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.HOME_TEXT.addTo(jsonObject, fHomeText);
    IJsonOption.AWAY_TEXT.addTo(jsonObject, fAwayText);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fHomeText = IJsonOption.HOME_TEXT.getFrom(jsonObject);
    fAwayText = IJsonOption.AWAY_TEXT.getFrom(jsonObject);
  }
  
}

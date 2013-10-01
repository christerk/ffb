package com.balancedbytes.games.ffb;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.JsonBooleanOption;
import com.balancedbytes.games.ffb.json.JsonIntOption;
import com.balancedbytes.games.ffb.json.JsonStringOption;
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
public class KnockoutRecovery implements IByteArraySerializable, IXmlSerializable {
  
  private static final JsonStringOption _JSON_PLAYER_ID = new JsonStringOption("playerId");
  private static final JsonBooleanOption _JSON_RECOVERING = new JsonBooleanOption("recovering");
  private static final JsonIntOption _JSON_ROLL = new JsonIntOption("roll");
  private static final JsonIntOption _JSON_BLOODWEISER_BABES = new JsonIntOption("bloodweiserBabes");
  
  public static final String XML_TAG = "knockoutRecovery";
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_RECOVERING = "recovering";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_BLOODWEISER_BABES = "bloodweiserBabes";
  
  private String fPlayerId;
  private boolean fRecovering;
  private int fRoll;
  private int fBloodweiserBabes;
  
  public KnockoutRecovery() {
    super();
  }

  public KnockoutRecovery(String pPlayerId, boolean pRecovering, int pRoll, int pBloodweiserBabes) {
    fPlayerId = pPlayerId;
    fRecovering = pRecovering;
    fRoll = pRoll;
    fBloodweiserBabes = pBloodweiserBabes;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isRecovering() {
    return fRecovering;
  }

  public int getRoll() {
    return fRoll;
  }
  
  public int getBloodweiserBabes() {
    return fBloodweiserBabes;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RECOVERING, isRecovering());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BLOODWEISER_BABES, getBloodweiserBabes());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);
  	UtilXml.endElement(pHandler, XML_TAG);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      fPlayerId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_PLAYER_ID);
      fRecovering = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_RECOVERING);
      fRoll = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_ROLL);
      fBloodweiserBabes = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_BLOODWEISER_BABES);
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
    pByteList.addBoolean(isRecovering());
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getBloodweiserBabes());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fRecovering = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fBloodweiserBabes = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    _JSON_PLAYER_ID.addTo(jsonObject, fPlayerId);
    _JSON_RECOVERING.addTo(jsonObject, fRecovering);
    _JSON_ROLL.addTo(jsonObject, fRoll);
    _JSON_BLOODWEISER_BABES.addTo(jsonObject, fBloodweiserBabes);
    return jsonObject;
  }
  
  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    fPlayerId = _JSON_PLAYER_ID.getFrom(jsonObject);
    fRecovering = _JSON_RECOVERING.getFrom(jsonObject);
    fRoll = _JSON_ROLL.getFrom(jsonObject);
    fBloodweiserBabes = _JSON_BLOODWEISER_BABES.getFrom(jsonObject);
  }

}

package com.balancedbytes.games.ffb.dialog;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DialogPettyCashParameter implements IDialogParameter {
  
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_TAG_TREASURY = "treasury";
  private static final String _XML_TAG_TEAM_VALUE = "teamValue";
  private static final String _XML_TAG_OPPONENT_TEAM_VALUE = "opponentTeamValue";
  
  private String fTeamId;
  private int fTreasury;
  private int fTeamValue;
  private int fOpponentTeamValue;
  
  public DialogPettyCashParameter() {
    super();
  }
  
  public DialogPettyCashParameter(String pTeamId, int pTeamValue, int pTreasury, int pOpponentTeamValue) {
    this();
    fTeamId = pTeamId;
    fTeamValue = pTeamValue;
    fTreasury = pTreasury;
    fOpponentTeamValue = pOpponentTeamValue;
  }
  
  public DialogId getId() {
    return DialogId.PETTY_CASH;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public int getTeamValue() {
    return fTeamValue;
  }
  
  public int getTreasury() {
    return fTreasury;
  }
  
  public int getOpponentTeamValue() {
    return fOpponentTeamValue;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogPettyCashParameter(getTeamId(), getTeamValue(), getTreasury(), getOpponentTeamValue());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, getTeamValue());
    UtilXml.addValueElement(pHandler, _XML_TAG_TREASURY, getTreasury());
    UtilXml.addValueElement(pHandler, _XML_TAG_OPPONENT_TEAM_VALUE, getOpponentTeamValue());
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getId().getId());
    pByteList.addString(getTeamId());
    pByteList.addInt(getTeamValue());
    pByteList.addInt(getTreasury());
    pByteList.addInt(getOpponentTeamValue());
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    UtilDialogParameter.validateDialogId(this, new DialogIdFactory().forId(pByteArray.getByte()));
    fTeamId = pByteArray.getString();
    fTeamValue = pByteArray.getInt();
    fTreasury = pByteArray.getInt();
    fOpponentTeamValue = pByteArray.getInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);
    IJsonOption.TREASURY.addTo(jsonObject, fTreasury);
    IJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, fOpponentTeamValue);
    return jsonObject;
  }
  
  public DialogPettyCashParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fTeamValue = IJsonOption.TEAM_VALUE.getFrom(jsonObject);
    fTreasury = IJsonOption.TREASURY.getFrom(jsonObject);
    fOpponentTeamValue = IJsonOption.OPPONENT_TEAM_VALUE.getFrom(jsonObject);
    return this;
  }

}

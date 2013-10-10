package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

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
public class ReportStandUpRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_RE_ROLLED = "reRolled";

  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private boolean fReRolled;
  
  public ReportStandUpRoll() {
    super();
  }

  public ReportStandUpRoll(String pPlayerId, boolean pSuccessful, int pRoll, boolean pReRolled) {
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fReRolled = pReRolled;
  }
  
  public ReportId getId() {
    return ReportId.STAND_UP_ROLL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  public boolean isReRolled() {
    return fReRolled;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportStandUpRoll(getPlayerId(), isSuccessful(), getRoll(), isReRolled());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLLED, isReRolled());
    UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addBoolean(isReRolled());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
    return jsonObject;
  }
  
  public ReportStandUpRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
    return this;
  }  
    
}

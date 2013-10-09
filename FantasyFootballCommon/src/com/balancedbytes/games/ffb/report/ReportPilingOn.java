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
public class ReportPilingOn implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_USED = "used";
  private static final String _XML_ATTRIBUTE_RE_ROLL_INJURY = "reRollInjury";

  private String fPlayerId;
  private boolean fUsed;
  private boolean fReRollInjury;
  
  public ReportPilingOn() {
    super();
  }

  public ReportPilingOn(String pPlayerId, boolean pUsed, boolean pReRollInjury) {
    fPlayerId = pPlayerId;
    fUsed = pUsed;
    fReRollInjury = pReRollInjury;
  }

  public ReportId getId() {
    return ReportId.PILING_ON;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isUsed() {
    return fUsed;
  }
  
  public boolean isReRollInjury() {
    return fReRollInjury;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportPilingOn(getPlayerId(), isUsed(), isReRollInjury());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, getPlayerId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USED, isUsed());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_RE_ROLL_INJURY, isReRollInjury());
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
    pByteList.addBoolean(isUsed());
    pByteList.addBoolean(isReRollInjury());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fUsed = pByteArray.getBoolean();
    fReRollInjury = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.USED.addTo(jsonObject, fUsed);
    IJsonOption.RE_ROLL_INJURY.addTo(jsonObject, fReRollInjury);
    return jsonObject;
  }
  
  public ReportPilingOn initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fUsed = IJsonOption.USED.getFrom(jsonObject);
    fReRollInjury = IJsonOption.RE_ROLL_INJURY.getFrom(jsonObject);
    return this;
  }
      
}

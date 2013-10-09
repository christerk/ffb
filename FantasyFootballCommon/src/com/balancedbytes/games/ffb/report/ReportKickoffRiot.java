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
public class ReportKickoffRiot implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_TURN_MODIFIER = "turnModifier";
  
  private int fRoll;
  private int fTurnModifier;
  
  public ReportKickoffRiot() {
    super();
  }

  public ReportKickoffRiot(int pRoll, int pTurnModifier) {
    fRoll = pRoll;
    fTurnModifier = pTurnModifier;
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_RIOT;
  }

  public int getRoll() {
    return fRoll;
  }
  
  public int getTurnModifier() {
    return fTurnModifier;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportKickoffRiot(getRoll(), getTurnModifier());
  }
    
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getRoll());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TURN_MODIFIER, getTurnModifier());
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
    pByteList.addByte((byte) getRoll());
    pByteList.addByte((byte) getTurnModifier());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRoll = pByteArray.getByte();
    fTurnModifier = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.TURN_MODIFIER.addTo(jsonObject, fTurnModifier);
    return jsonObject;
  }
  
  public ReportKickoffRiot initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fTurnModifier = IJsonOption.TURN_MODIFIER.getFrom(jsonObject);
    return this;
  }
    
}

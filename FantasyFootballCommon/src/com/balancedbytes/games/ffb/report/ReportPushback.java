package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.PushbackMode;
import com.balancedbytes.games.ffb.PushbackModeFactory;
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
public class ReportPushback implements IReport {
  
  private static final String _XML_ATTRIBUTE_DEFENDER_ID = "defenderId";
  private static final String _XML_ATTRIBUTE_MODE = "mode";
  
  private String fDefenderId;
  private PushbackMode fPushbackMode;
  
  public ReportPushback() {
    super();
  }

  public ReportPushback(String pDefenderId, PushbackMode pMode) {
    this();
    fDefenderId = pDefenderId;
    fPushbackMode = pMode;
  }
  
  public ReportId getId() {
    return ReportId.PUSHBACK;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
  
  public PushbackMode getPushbackMode() {
    return fPushbackMode;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportPushback(getDefenderId(), getPushbackMode());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFENDER_ID, getDefenderId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MODE, (getPushbackMode() != null) ? getPushbackMode().getName() : null);
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
    pByteList.addString(getDefenderId());
    pByteList.addByte((byte) ((getPushbackMode() != null) ? getPushbackMode().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    fPushbackMode = new PushbackModeFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    IJsonOption.PUSHBACK_MODE.addTo(jsonObject, fPushbackMode);
    return jsonObject;
  }
  
  public ReportPushback initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    fPushbackMode = (PushbackMode) IJsonOption.PUSHBACK_MODE.getFrom(jsonObject);
    return this;
  }    
 
}

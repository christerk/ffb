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
public class ReportCoinThrow implements IReport {

  private static final String _XML_ATTRIBUTE_COIN_THROW_HEADS = "coinThrowHeads";
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_COIN_CHOICE_HEADS = "coinChoiceHeads";
  
  private boolean fCoinThrowHeads;
  private String fCoach;
  private boolean fCoinChoiceHeads;
  
  public ReportCoinThrow() {
    super();
  }
  
  public ReportCoinThrow(boolean pCoinThrowHeads, String pCoach, boolean pCoinChoiceHeads) {
    fCoinThrowHeads = pCoinThrowHeads;
    fCoach = pCoach;
    fCoinChoiceHeads = pCoinChoiceHeads;
  }
  
  public ReportId getId() {
    return ReportId.COIN_THROW;
  }
  
  public boolean isCoinThrowHeads() {
    return fCoinThrowHeads;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public boolean isCoinChoiceHeads() {
    return fCoinChoiceHeads;
  }

  // transformation
  
  public IReport transform() {
    return new ReportCoinThrow(isCoinThrowHeads(), getCoach(), isCoinChoiceHeads());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COIN_THROW_HEADS, isCoinThrowHeads());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COIN_CHOICE_HEADS, isCoinChoiceHeads());
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
    pByteList.addBoolean(isCoinThrowHeads());
    pByteList.addString(getCoach());
    pByteList.addBoolean(isCoinChoiceHeads());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCoinThrowHeads = pByteArray.getBoolean();
    fCoach = pByteArray.getString();
    fCoinChoiceHeads = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
    
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.COIN_THROW_HEADS.addTo(jsonObject, fCoinThrowHeads);
    IJsonOption.COIN_CHOICE_HEADS.addTo(jsonObject, fCoinChoiceHeads);
    return jsonObject;
  }
  
  public ReportCoinThrow initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fCoinThrowHeads = IJsonOption.COIN_THROW_HEADS.getFrom(jsonObject);
    fCoinChoiceHeads = IJsonOption.COIN_CHOICE_HEADS.getFrom(jsonObject);
    return this;
  }
 
}
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
public class ReportDoubleHiredStarPlayer implements IReport {
  
  private static final String _XML_ATTRIBUTE_STAR_PLAYER_NAME = "starPlayerName";
  
  private String fStarPlayerName;
  
  public ReportDoubleHiredStarPlayer() {
    super();
  }
  
  public ReportDoubleHiredStarPlayer(String pStarPlayerName) {
    fStarPlayerName = pStarPlayerName;
  }

  public ReportId getId() {
    return ReportId.DOUBLE_HIRED_STAR_PLAYER;
  }
  
  public String getStarPlayerName() {
    return fStarPlayerName;
  }

  // transformation
  
  public IReport transform() {
    return new ReportDoubleHiredStarPlayer(getStarPlayerName());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STAR_PLAYER_NAME, getStarPlayerName());
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
    pByteList.addString(getStarPlayerName());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fStarPlayerName = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.STAR_PLAYER_NAME.addTo(jsonObject, fStarPlayerName);
    return jsonObject;
  }
  
  public ReportDoubleHiredStarPlayer initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fStarPlayerName = IJsonOption.STAR_PLAYER_NAME.getFrom(jsonObject);
    return this;
  }
    
}

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
public class ReportBlockRoll implements IReport {
  
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_CHOOSING_TEAM_ID = "choosingTeamId";
  
  private int[] fBlockRoll;
  private String fChoosingTeamId;
  
  public ReportBlockRoll() {
    super();
  }

  public ReportBlockRoll(String pChoosingTeamId, int[] pBlockRoll) {
    fChoosingTeamId = pChoosingTeamId;
    fBlockRoll = pBlockRoll;
  }
  
  public ReportId getId() {
    return ReportId.BLOCK_ROLL;
  }
  
  public String getChoosingTeamId() {
    return fChoosingTeamId;
  }
  
  public int[] getBlockRoll() {
    return fBlockRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportBlockRoll(getChoosingTeamId(), getBlockRoll());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CHOOSING_TEAM_ID, getChoosingTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, getBlockRoll());
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
    pByteList.addString(getChoosingTeamId());
    pByteList.addByteArray(getBlockRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoosingTeamId = pByteArray.getString();
    fBlockRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.CHOOSING_TEAM_ID.addTo(jsonObject, fChoosingTeamId);
    IJsonOption.BLOCK_ROLL.addTo(jsonObject, fBlockRoll);
    return jsonObject;
  }
  
  public ReportBlockRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fChoosingTeamId = IJsonOption.CHOOSING_TEAM_ID.getFrom(jsonObject);
    fBlockRoll = IJsonOption.BLOCK_ROLL.getFrom(jsonObject);
    return this;
  }
    
}

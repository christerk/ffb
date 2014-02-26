package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportBlock implements IReport {

  private String fDefenderId;
  
  public ReportBlock() {
    super();
  }
  
  public ReportBlock(String pDefenderId) {
    fDefenderId = pDefenderId;
  }
  
  public ReportId getId() {
    return ReportId.BLOCK;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }
    
  // transformation
  
  public IReport transform() {
    return new ReportBlock(getDefenderId());
  }
    
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    if (byteArraySerializationVersion < 2) {
    	pByteArray.getBoolean();  // deprecated flag usingHorns
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    return jsonObject;
  }
  
  public ReportBlock initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    return this;
  }

}

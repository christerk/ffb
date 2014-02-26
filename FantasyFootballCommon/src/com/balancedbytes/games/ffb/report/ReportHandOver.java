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
public class ReportHandOver implements IReport {
  
  private String fCatcherId;
  
  public ReportHandOver() {
    super();
  }

  public ReportHandOver(String pCatcherId) {
    fCatcherId = pCatcherId;
  }
  
  public ReportId getId() {
    return ReportId.HAND_OVER;
  }
  
  public String getCatcherId() {
    return fCatcherId;
  }

  // transformation
  
  public IReport transform() {
    return new ReportHandOver(getCatcherId());
  }
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fCatcherId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    return jsonObject;
  }
  
  public ReportHandOver initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCatcherId = IJsonOption.CATCHER_ID.getFrom(jsonObject);
    return this;
  }
    
}

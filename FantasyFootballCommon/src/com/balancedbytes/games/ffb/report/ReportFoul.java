package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportFoul implements IReport {
  
  private String fDefenderId;
  
  public ReportFoul() {
    super();
  }
  
  public ReportFoul(String pDefenderId) {
    fDefenderId = pDefenderId;
  }

  public ReportId getId() {
    return ReportId.FOUL;
  }
  
  public String getDefenderId() {
    return fDefenderId;
  }

  // transformation
  
  public IReport transform() {
    return new ReportFoul(getDefenderId());
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getDefenderId());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fDefenderId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    return jsonObject;
  }
  
  public ReportFoul initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fDefenderId = IJsonOption.DEFENDER_ID.getFrom(jsonObject);
    return this;
  }
    
}

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
public class ReportStartHalf implements IReport {

  private int fHalf;

  public ReportStartHalf() {
    super();
  }

  public ReportStartHalf(int pHalf) {
    fHalf = pHalf;
  }

  public ReportId getId() {
    return ReportId.START_HALF;
  }
  
  public int getHalf() {
    return fHalf;
  }

  // transformation

  public IReport transform() {
    return new ReportStartHalf(getHalf());
  }

  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fHalf = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.HALF.addTo(jsonObject, fHalf);
    return jsonObject;
  }
  
  public ReportStartHalf initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fHalf = IJsonOption.HALF.getFrom(jsonObject);
    return this;
  }

}

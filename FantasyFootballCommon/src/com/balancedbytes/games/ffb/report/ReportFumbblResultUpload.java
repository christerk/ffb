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
public class ReportFumbblResultUpload implements IReport {
  
  private boolean fSuccessful;
  private String fUploadStatus;
  
  public ReportFumbblResultUpload() {
    super();
  }
  
  public ReportFumbblResultUpload(boolean pSuccessful, String pStatus) {
    fSuccessful = pSuccessful;
    fUploadStatus = pStatus;
  }
  
  public ReportId getId() {
    return ReportId.FUMBBL_RESULT_UPLOAD;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public String getUploadStatus() {
    return fUploadStatus;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportFumbblResultUpload(isSuccessful(), getUploadStatus());
  }

  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSuccessful = pByteArray.getBoolean();
    fUploadStatus = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.UPLOAD_STATUS.addTo(jsonObject, fUploadStatus);
    return jsonObject;
  }
  
  public ReportFumbblResultUpload initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fUploadStatus = IJsonOption.UPLOAD_STATUS.getFrom(jsonObject);
    return this;
  }
  
}

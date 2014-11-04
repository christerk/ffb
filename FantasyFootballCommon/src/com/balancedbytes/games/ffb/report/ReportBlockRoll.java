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
public class ReportBlockRoll implements IReport {
  
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
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fChoosingTeamId = pByteArray.getString();
    fBlockRoll = pByteArray.getByteArrayAsIntArray();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
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

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
public class ReportDoubleHiredStarPlayer implements IReport {
  
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

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
public class ReportStandUpRoll implements IReport {
  
  private String fPlayerId;
  private boolean fSuccessful;
  private int fRoll;
  private boolean fReRolled;
  
  public ReportStandUpRoll() {
    super();
  }

  public ReportStandUpRoll(String pPlayerId, boolean pSuccessful, int pRoll, boolean pReRolled) {
    fPlayerId = pPlayerId;
    fSuccessful = pSuccessful;
    fRoll = pRoll;
    fReRolled = pReRolled;
  }
  
  public ReportId getId() {
    return ReportId.STAND_UP_ROLL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  public boolean isReRolled() {
    return fReRolled;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportStandUpRoll(getPlayerId(), isSuccessful(), getRoll(), isReRolled());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
    pByteList.addBoolean(isReRolled());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    fReRolled = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.RE_ROLLED.addTo(jsonObject, fReRolled);
    return jsonObject;
  }
  
  public ReportStandUpRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fReRolled = IJsonOption.RE_ROLLED.getFrom(jsonObject);
    return this;
  }  
    
}

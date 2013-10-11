package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSourceFactory;
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
public class ReportReRoll implements IReport {
  
  private String fPlayerId;
  private ReRollSource fReRollSource;
  private boolean fSuccessful;
  private int fRoll;
  
  public ReportReRoll() {
    super();
  }

  public ReportReRoll(String pPlayerId, ReRollSource pReRollSource, boolean pSuccessful, int pRoll) {
    fPlayerId = pPlayerId;
    fReRollSource = pReRollSource;    
    fSuccessful = pSuccessful;
    fRoll = pRoll;
  }
  
  public ReportId getId() {
    return ReportId.RE_ROLL;
  }

  public String getPlayerId() {
    return fPlayerId;
  }
  
  public ReRollSource getReRollSource() {
    return fReRollSource;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReRoll(getPlayerId(), getReRollSource(), isSuccessful(), getRoll());
  }
    
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addByte((byte) ((getReRollSource() != null) ? getReRollSource().getId() : 0));
    pByteList.addBoolean(isSuccessful());
    pByteList.addByte((byte) getRoll());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fReRollSource = new ReRollSourceFactory().forId(pByteArray.getByte());
    fSuccessful = pByteArray.getBoolean();
    fRoll = pByteArray.getByte();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    return jsonObject;
  }
  
  public ReportReRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    return this;
  }
      
}

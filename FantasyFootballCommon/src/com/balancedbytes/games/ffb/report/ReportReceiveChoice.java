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
public class ReportReceiveChoice implements IReport {
  
  private String fTeamId;
  private boolean fReceiveChoice;
  
  public ReportReceiveChoice() {
    super();
  }

  public ReportReceiveChoice(String pTeamId, boolean pChoiceReceive) {
    fTeamId = pTeamId;
    fReceiveChoice = pChoiceReceive;
  }
  
  public ReportId getId() {
    return ReportId.RECEIVE_CHOICE;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public boolean isReceiveChoice() {
    return fReceiveChoice;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReceiveChoice(getTeamId(), isReceiveChoice());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTeamId());
    pByteList.addBoolean(isReceiveChoice());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fReceiveChoice = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.RECEIVE_CHOICE.addTo(jsonObject, fReceiveChoice);
    return jsonObject;
  }
  
  public ReportReceiveChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fReceiveChoice = IJsonOption.RECEIVE_CHOICE.getFrom(jsonObject);
    return this;
  }
    
}

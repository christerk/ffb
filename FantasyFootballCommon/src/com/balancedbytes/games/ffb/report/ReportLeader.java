package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ReportLeader implements IReport {

  private String fTeamId;
  private LeaderState fLeaderState;

  public ReportLeader() {
    super();
  }

  public ReportLeader(String pTeamId, LeaderState pLeaderState) {
    fTeamId = pTeamId;
    fLeaderState = pLeaderState;
  }

  public ReportId getId() {
    return ReportId.LEADER;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public LeaderState getLeaderState() {
    return fLeaderState;
  }

  // transformation

  public IReport transform() {
    return new ReportLeader(getTeamId(), getLeaderState());
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getTeamId());
    pByteList.addString(getLeaderState().toString());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fLeaderState = LeaderState.valueOf(pByteArray.getString());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.LEADER_STATE.addTo(jsonObject, fLeaderState);
    return jsonObject;
  }
  
  public ReportLeader initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fLeaderState = (LeaderState) IJsonOption.LEADER_STATE.getFrom(jsonObject);
    return this;
  }
  
}

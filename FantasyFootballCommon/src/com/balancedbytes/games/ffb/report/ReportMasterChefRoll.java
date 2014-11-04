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
public class ReportMasterChefRoll implements IReport {

  private String fTeamId;
  private int[] fMasterChefRoll;
  private int fReRollsStolen;

  public ReportMasterChefRoll() {
    super();
  }

  public ReportMasterChefRoll(String pTeamId, int[] pRoll, int pReRollsStolen) {
    fTeamId = pTeamId;
    fMasterChefRoll = pRoll;
    fReRollsStolen = pReRollsStolen;
  }

  public ReportId getId() {
    return ReportId.MASTER_CHEF_ROLL;
  }

  public String getTeamId() {
    return fTeamId;
  }

  public int[] getMasterChefRoll() {
    return fMasterChefRoll;
  }

  public int getReRollsStolen() {
    return fReRollsStolen;
  }

  // transformation

  public IReport transform() {
    return new ReportMasterChefRoll(getTeamId(), getMasterChefRoll(), getReRollsStolen());
  }

  // ByteArray serialization

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fTeamId = pByteArray.getString();
    fMasterChefRoll = pByteArray.getByteArrayAsIntArray();
    fReRollsStolen = pByteArray.getByte();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.MASTER_CHEF_ROLL.addTo(jsonObject, fMasterChefRoll);
    IJsonOption.RE_ROLLS_STOLEN.addTo(jsonObject, fReRollsStolen);
    return jsonObject;
  }

  public ReportMasterChefRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fMasterChefRoll = IJsonOption.MASTER_CHEF_ROLL.getFrom(jsonObject);
    fReRollsStolen = IJsonOption.RE_ROLLS_STOLEN.getFrom(jsonObject);
    return this;
  }

}

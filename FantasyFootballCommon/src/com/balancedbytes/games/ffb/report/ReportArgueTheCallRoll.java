package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportArgueTheCallRoll implements IReport {
  
  private String fPlayerId;
  private boolean fSuccessful;
  private boolean fCoachBanned;
  private int fRoll;
  
  public ReportArgueTheCallRoll() {
    super();
  }

  public ReportArgueTheCallRoll(String playerId, boolean successful, boolean coachBanned, int roll) {
    fPlayerId = playerId;
    fSuccessful = successful;
    fCoachBanned = coachBanned;
    fRoll = roll;
  }
  
  public ReportId getId() {
    return ReportId.ARGUE_THE_CALL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public boolean isCoachBanned() {
    return fCoachBanned;
  }
  
  public int getRoll() {
    return fRoll;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportArgueTheCallRoll(getPlayerId(), isSuccessful(), isCoachBanned(), getRoll());
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.COACH_BANNED.addTo(jsonObject, fCoachBanned);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    return jsonObject;
  }
  
  public ReportArgueTheCallRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fCoachBanned = IJsonOption.COACH_BANNED.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    return this;
  }
    
}

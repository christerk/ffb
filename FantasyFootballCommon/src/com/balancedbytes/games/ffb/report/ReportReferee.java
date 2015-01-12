package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportReferee implements IReport {
  
  private boolean fFoulingPlayerBanned;
  
  public ReportReferee() {
    super();
  }
  
  public ReportReferee(boolean pFoulingPlayerBanned) {
    fFoulingPlayerBanned = pFoulingPlayerBanned;
  }
  
  public ReportId getId() {
    return ReportId.REFEREE;
  }
  
  public boolean isFoulingPlayerBanned() {
    return fFoulingPlayerBanned;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportReferee(isFoulingPlayerBanned());
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.FOULING_PLAYER_BANNED.addTo(jsonObject, fFoulingPlayerBanned);
    return jsonObject;
  }
  
  public ReportReferee initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fFoulingPlayerBanned = IJsonOption.FOULING_PLAYER_BANNED.getFrom(jsonObject);
    return this;
  }
  
}

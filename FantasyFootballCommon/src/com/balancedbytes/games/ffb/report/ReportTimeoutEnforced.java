package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportTimeoutEnforced implements IReport {
  
  private String fCoach;
  
  public ReportTimeoutEnforced() {
    super();
  }
  
  public ReportTimeoutEnforced(String pCoach) {
    fCoach = pCoach;
  }

  public ReportId getId() {
    return ReportId.TIMEOUT_ENFORCED;
  }

  public String getCoach() {
    return fCoach;
  }

  // transformation
  
  public IReport transform() {
    return new ReportTimeoutEnforced(getCoach());
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    return jsonObject;
  }
  
  public ReportTimeoutEnforced initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    return this;
  }  
    
}

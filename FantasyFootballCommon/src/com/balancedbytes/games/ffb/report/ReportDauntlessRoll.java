package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ReportDauntlessRoll extends ReportSkillRoll {
  
  private int fStrength;

  public ReportDauntlessRoll() {
    super(ReportId.DAUNTLESS_ROLL);
  }

  public ReportDauntlessRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, int pStrength) {    
    super(ReportId.DAUNTLESS_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled);
    fStrength = pStrength;
  }
  
  public ReportId getId() {
    return ReportId.DAUNTLESS_ROLL;
  }

  public int getStrength() {
    return fStrength;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportDauntlessRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), getStrength());
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
    IJsonOption.STRENGTH.addTo(jsonObject, fStrength);
    return jsonObject;
  }
  
  @Override
  public ReportDauntlessRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fStrength = IJsonOption.STRENGTH.getFrom(jsonObject);
    return this;
  }

}

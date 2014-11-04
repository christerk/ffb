package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.InterceptionModifier;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportInterceptionRoll extends ReportSkillRoll {

  private boolean fBomb;

  public ReportInterceptionRoll() {
    super(ReportId.INTERCEPTION_ROLL);
  }

  public ReportInterceptionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, InterceptionModifier[] pModifiers, boolean pBomb) {
    super(ReportId.INTERCEPTION_ROLL, pPlayerId, pSuccessful,pRoll, pMinimumRoll, pReRolled, pModifiers);
    fBomb = pBomb;
  }

  public ReportId getId() {
    return ReportId.INTERCEPTION_ROLL;
  }

  @Override
  public InterceptionModifier[] getRollModifiers() {
    return getRollModifierList().toArray(new InterceptionModifier[getRollModifierList().size()]);
  }

  public boolean isBomb() {
    return fBomb;
  }

  // transformation

  public IReport transform() {
    return new ReportInterceptionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), getRollModifiers(), isBomb());
  }

  // ByteArray serialization

  @Override
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    if (byteArraySerializationVersion > 1) {
      fBomb = pByteArray.getBoolean();
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
    IJsonOption.BOMB.addTo(jsonObject, fBomb);
    return jsonObject;
  }
  
  @Override
  public ReportInterceptionRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fBomb = IJsonOption.BOMB.getFrom(jsonObject);
    return this;
  }

}

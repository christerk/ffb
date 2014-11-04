package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportSpecialEffectRoll implements IReport {

  private SpecialEffect fSpecialEffect;
  private String fPlayerId;
  private int fRoll;
  private boolean fSuccessful;

  public ReportSpecialEffectRoll() {
    super();
  }

  public ReportSpecialEffectRoll(SpecialEffect pSpecialEffect, String pPlayerId, int pRoll, boolean pSuccessful) {
    fSpecialEffect = pSpecialEffect;
    fPlayerId = pPlayerId;
    fRoll = pRoll;
    fSuccessful = pSuccessful;
  }

  public ReportId getId() {
    return ReportId.SPELL_EFFECT_ROLL;
  }

  public SpecialEffect getSpecialEffect() {
    return fSpecialEffect;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public int getRoll() {
    return fRoll;
  }

  public boolean isSuccessful() {
    return fSuccessful;
  }

  // transformation

  public IReport transform() {
    return new ReportSpecialEffectRoll(getSpecialEffect(), getPlayerId(), getRoll(), isSuccessful());
  }

  // ByteArray serialization

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSpecialEffect = new SpecialEffectFactory().forId(pByteArray.getByte());
    fPlayerId = pByteArray.getString();
    fRoll = pByteArray.getByte();
    fSuccessful = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.SPECIAL_EFFECT.addTo(jsonObject, fSpecialEffect);
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.ROLL.addTo(jsonObject, fRoll);
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    return jsonObject;
  }
  
  public ReportSpecialEffectRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fSpecialEffect = (SpecialEffect) IJsonOption.SPECIAL_EFFECT.getFrom(jsonObject);
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fRoll = IJsonOption.ROLL.getFrom(jsonObject);
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    return this;
  }

}

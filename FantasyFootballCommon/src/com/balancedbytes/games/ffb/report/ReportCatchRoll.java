package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.CatchModifier;
import com.balancedbytes.games.ffb.CatchModifierFactory;
import com.balancedbytes.games.ffb.IRollModifierFactory;
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
public class ReportCatchRoll extends ReportSkillRoll {

  private boolean fBomb;

  public ReportCatchRoll() {
    super(ReportId.CATCH_ROLL);
  }

  public ReportCatchRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled, boolean pBomb, CatchModifier[] pModifiers) {
    super(ReportId.CATCH_ROLL, pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled);
    fBomb = pBomb;
    addRollModifiers(pModifiers);
  }

  public CatchModifier[] getCatchModifiers() {
    return getRollModifiers().toArray(new CatchModifier[getRollModifiers().size()]);
  }

  public boolean hasCatchModifier(CatchModifier pModifier) {
    return getRollModifiers().contains(pModifier);
  }

  public boolean isBomb() {
    return fBomb;
  }
  
  @Override
  protected IRollModifierFactory createRollModifierFactory() {
    return new CatchModifierFactory();
  }

  // transformation

  public IReport transform() {
    return new ReportCatchRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(), isBomb(), getCatchModifiers());
  }

  // ByteArray serialization

  @Override
  public int getByteArraySerializationVersion() {
    return 2;
  }

  @Override
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addBoolean(fBomb);
  }

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
  public JsonValue toJsonValue() {
    JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
    IJsonOption.BOMB.addTo(jsonObject, fBomb);
    return jsonObject;
  }
  
  @Override
  public ReportCatchRoll initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fBomb = IJsonOption.BOMB.getFrom(jsonObject);
    return this;
  }

}

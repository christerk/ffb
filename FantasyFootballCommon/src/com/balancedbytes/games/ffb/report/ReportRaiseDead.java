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
public class ReportRaiseDead implements IReport {

  private String fPlayerId;
  private boolean fNurglesRot;

  public ReportRaiseDead() {
    super();
  }

  public ReportRaiseDead(String pPlayerId, boolean pNurglesRot) {
    fPlayerId = pPlayerId;
    fNurglesRot = pNurglesRot;
  }

  public ReportId getId() {
    return ReportId.RAISE_DEAD;
  }

  public String getPlayerId() {
    return fPlayerId;
  }

  public boolean isNurglesRot() {
    return fNurglesRot;
  }

  // transformation

  public IReport transform() {
    return new ReportRaiseDead(getPlayerId(), isNurglesRot());
  }

  // ByteArray serialization

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fNurglesRot = pByteArray.getBoolean();
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.NURGLES_ROT.addTo(jsonObject, fNurglesRot);
    return jsonObject;
  }

  public ReportRaiseDead initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fNurglesRot = IJsonOption.NURGLES_ROT.getFrom(jsonObject);
    return this;
  }

}

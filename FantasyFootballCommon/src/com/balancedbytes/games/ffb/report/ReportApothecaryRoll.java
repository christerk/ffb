package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
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
public class ReportApothecaryRoll implements IReport {
  
  private String fPlayerId;
  private int[] fCasualtyRoll;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;

  public ReportApothecaryRoll() {
    super();
  }

  public ReportApothecaryRoll(String pPlayerId, int[] pCasualtyRoll, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fCasualtyRoll = pCasualtyRoll;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public ReportId getId() {
    return ReportId.APOTHECARY_ROLL;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }

  public int[] getCasualtyRoll() {
    return fCasualtyRoll;
  }

  public PlayerState getPlayerState() {
    return fPlayerState;
  }

  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportApothecaryRoll(getPlayerId(), getCasualtyRoll(), getPlayerState(), getSeriousInjury());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addByteArray(getCasualtyRoll());
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fCasualtyRoll = pByteArray.getByteArrayAsIntArray();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
    IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
    IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
    return jsonObject;
  }
  
  public ReportApothecaryRoll initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(jsonObject);
    fPlayerState = IJsonOption.PLAYER_STATE.getFrom(jsonObject);
    fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(jsonObject);
    return this;
  }
    
}

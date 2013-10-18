package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportKickoffThrowARock implements IReport {
  
  private int fRollHome;
  private int fRollAway;
  private List<String> fPlayersHit;
  
  public ReportKickoffThrowARock() {
    fPlayersHit = new ArrayList<String>();
  }

  public ReportKickoffThrowARock(int pRollHome, int pRollAway, String[] pPlayersHit) {
    this();
    fRollHome = pRollHome;
    fRollAway = pRollAway;
    addPlayerIds(pPlayersHit);
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_THROW_A_ROCK;
  }

  public int getRollHome() {
    return fRollHome;
  }
  
  public int getRollAway() {
    return fRollAway;
  }
  
  public String[] getPlayersHit() {
    return fPlayersHit.toArray(new String[fPlayersHit.size()]);
  }
  
  private void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayersHit.add(pPlayerId);
    }
  }
  
  private void addPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addPlayerId(playerId);
      }
    }
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportKickoffThrowARock(getRollAway(), getRollHome(), getPlayersHit());
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getRollHome());
    pByteList.addByte((byte) getRollAway());
    pByteList.addStringArray(getPlayersHit());
  }
    
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByte();
    fRollAway = pByteArray.getByte();
    addPlayerIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.ROLL_HOME.addTo(jsonObject, fRollHome);
    IJsonOption.ROLL_AWAY.addTo(jsonObject, fRollAway);
    IJsonOption.PLAYER_IDS_HIT.addTo(jsonObject, fPlayersHit);
    return jsonObject;
  }
  
  public ReportKickoffThrowARock initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fRollHome = IJsonOption.ROLL_HOME.getFrom(jsonObject);
    fRollAway = IJsonOption.ROLL_AWAY.getFrom(jsonObject);
    fPlayersHit.clear();
    addPlayerIds(IJsonOption.PLAYER_IDS_HIT.getFrom(jsonObject));
    return this;
  }
      
}

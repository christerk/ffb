package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportSecretWeaponBan implements IReport {

  private List<String> fPlayerIds;
  private List<Integer> fRolls;
  private List<Boolean> fBans;
  
  public ReportSecretWeaponBan() {
    fPlayerIds = new ArrayList<String>();
    fRolls = new ArrayList<Integer>();
    fBans = new ArrayList<Boolean>();
  }

  public ReportId getId() {
    return ReportId.SECRET_WEAPON_BAN;
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  public int[] getRolls() {
  	int[] result = new int[fRolls.size()];
  	for (int i = 0; i < result.length; i++) {
  		result[i] = fRolls.get(i);
  	}
  	return result;
  }
  
  
  public boolean[] getBans() {
  	boolean[] result = new boolean[fBans.size()];
  	for (int i = 0; i < result.length; i++) {
  		result[i] = fBans.get(i);
  	}
  	return result;
  }
  
  public void add(String pPlayerId, int pRoll, boolean pBanned) {
  	fPlayerIds.add(pPlayerId);
  	fRolls.add(pRoll);
  	fBans.add(pBanned);
  }
  
  private void addPlayerIds(String[] pPlayerIds) {
    if (pPlayerIds != null) {
      for (String playerId : pPlayerIds) {
        fPlayerIds.add(playerId);
      }
    }
  }

  private void addRolls(int[] pRolls) {
    if (pRolls != null) {
      for (int roll : pRolls) {
        fRolls.add(roll);
      }
    }
  }
  
  private void addBans(boolean[] pBans) {
    if (pBans != null) {
      for (boolean ban : pBans) {
        fBans.add(ban);
      }
    }
  }

  // transformation
  
  public ReportSecretWeaponBan transform() {
  	ReportSecretWeaponBan transformed = new ReportSecretWeaponBan();
  	String[] playerIds = getPlayerIds();
  	int[] rolls = getRolls();
  	boolean[] banned = getBans();
  	for (int i = 0; i < playerIds.length; i++) {
  		transformed.add(playerIds[i], rolls[i], banned[i]);
  	}
    return transformed;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
    IJsonOption.ROLLS.addTo(jsonObject, fRolls);
    IJsonOption.BAN_ARRAY.addTo(jsonObject, fBans);
    return jsonObject;
  }
  
  public ReportSecretWeaponBan initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fPlayerIds.clear();
    addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(jsonObject));
    fRolls.clear();
    addRolls(IJsonOption.ROLLS.getFrom(jsonObject));
    fBans.clear();
    addBans(IJsonOption.BAN_ARRAY.getFrom(jsonObject));
    return this;
  }
  
}

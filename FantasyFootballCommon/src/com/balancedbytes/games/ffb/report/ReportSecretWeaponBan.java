package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportSecretWeaponBan implements IReport {

  private static final String _XML_TAG_BAN = "ban";
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "playerId";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_BANNED = "banned";

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
  
  // XML serialization
    
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] playerIds = getPlayerIds();
    int[] rolls = getRolls();
    boolean[] banned = getBans();
    if (ArrayTool.isProvided(playerIds)) {
      for (int i = 0; i < playerIds.length; i++) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, playerIds[i]);
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_BANNED, banned[i]);
        if (rolls[i] > 0) {
          UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, rolls[i]);
        }
        UtilXml.addEmptyElement(pHandler, _XML_TAG_BAN, attributes);
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 2;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addStringArray(getPlayerIds());
    pByteList.addByteArray(getRolls());
    pByteList.addBooleanArray(getBans());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    String[] playerIds = pByteArray.getStringArray();
    if (byteArraySerializationVersion > 1) {
    	int[] rolls = pByteArray.getByteArrayAsIntArray();
    	boolean[] banned = pByteArray.getBooleanArray();
    	for (int i = 0; i < playerIds.length; i++) {
    		add(playerIds[i], rolls[i], banned[i]);
    	}
    } else {
    	for (int i = 0; i < playerIds.length; i++) {
    		add(playerIds[i], 0, true);
    	}
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_IDS.addTo(jsonObject, fPlayerIds);
    IJsonOption.ROLLS.addTo(jsonObject, fRolls);
    IJsonOption.BANS.addTo(jsonObject, fBans);
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
    addBans(IJsonOption.BANS.getFrom(jsonObject));
    return this;
  }
  
}

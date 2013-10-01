package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.xml.UtilXml;




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
  private List<Boolean> fBanned;
  
  public ReportSecretWeaponBan() {
    fPlayerIds = new ArrayList<String>();
    fRolls = new ArrayList<Integer>();
    fBanned = new ArrayList<Boolean>();
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
  
  
  public boolean[] getBanned() {
  	boolean[] result = new boolean[fBanned.size()];
  	for (int i = 0; i < result.length; i++) {
  		result[i] = fBanned.get(i);
  	}
  	return result;
  }
  
  public void add(String pPlayerId, int pRoll, boolean pBanned) {
  	fPlayerIds.add(pPlayerId);
  	fRolls.add(pRoll);
  	fBanned.add(pBanned);
  }
      
  // transformation
  
  public ReportSecretWeaponBan transform() {
  	ReportSecretWeaponBan transformed = new ReportSecretWeaponBan();
  	String[] playerIds = getPlayerIds();
  	int[] rolls = getRolls();
  	boolean[] banned = getBanned();
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
    boolean[] banned = getBanned();
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
    pByteList.addBooleanArray(getBanned());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
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
  
}

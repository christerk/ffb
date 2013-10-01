package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;




/**
 * 
 * @author Kalimar
 */
public class ReportDefectingPlayers implements IReport {

  private static final String _XML_TAG_PLAYER = "player";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  private static final String _XML_ATTRIBUTE_DEFECTING = "defecting";

  private List<String> fPlayerIds;
  private List<Integer> fRolls;
  private List<Boolean> fDefecting;
  
  public ReportDefectingPlayers() {
    fPlayerIds = new ArrayList<String>();
    fRolls = new ArrayList<Integer>();
    fDefecting = new ArrayList<Boolean>();
  }

  public ReportDefectingPlayers(String[] pPlayerIds, int[] pRolls, boolean[] pDefecting) {
    this();
    addPlayerIds(pPlayerIds);
    addRolls(pRolls);
    addDefecting(pDefecting);
  }
  
  public ReportId getId() {
    return ReportId.DEFECTING_PLAYERS;
  }

  public int[] getRolls() {
    int[] rolls = new int[fRolls.size()];
    for (int i = 0; i < rolls.length; i++) {
      rolls[i] = fRolls.get(i);
    }
    return rolls;
  }
  
  private void addRoll(int pRoll) {
    fRolls.add(pRoll);
  }
  
  private void addRolls(int[] pRolls) {
    if (ArrayTool.isProvided(pRolls)) {
      for (int roll : pRolls) {
        addRoll(roll);
      }
    }
  }
  
  public boolean[] getDefecting() {
    boolean[] defecting = new boolean[fDefecting.size()];
    for (int i = 0; i < defecting.length; i++) {
      defecting[i] = fDefecting.get(i);
    }
    return defecting;
  }
  
  private void addDefecting(boolean pDefecting) {
    fDefecting.add(pDefecting);
  }
  
  private void addDefecting(boolean[] pDefecting) {
    if (ArrayTool.isProvided(pDefecting)) {
      for (boolean defecting : pDefecting) {
        addDefecting(defecting);
      }
    }
  }
  
  public String[] getPlayerIds() {
    return fPlayerIds.toArray(new String[fPlayerIds.size()]);
  }
  
  private void addPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIds.add(pPlayerId);
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
  
  public ReportDefectingPlayers transform() {
    return new ReportDefectingPlayers(getPlayerIds(), getRolls(), getDefecting());
  }
  
  // XML serialization
    
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] playerIds = getPlayerIds();
    if (ArrayTool.isProvided(playerIds)) {
      int[] rolls = getRolls();
      boolean[] defecting = getDefecting();
      for (int i = 0; i < playerIds.length; i++) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, playerIds[i]);
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, rolls[i]);
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DEFECTING, defecting[i]);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
      }
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addStringArray(getPlayerIds());
    pByteList.addByteArray(getRolls());
    pByteList.addBooleanArray(getDefecting());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    addPlayerIds(pByteArray.getStringArray());
    addRolls(pByteArray.getByteArrayAsIntArray());
    addDefecting(pByteArray.getBooleanArray());
    return byteArraySerializationVersion;
  }
  
}

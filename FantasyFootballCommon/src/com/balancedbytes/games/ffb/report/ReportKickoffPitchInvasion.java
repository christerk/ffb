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
public class ReportKickoffPitchInvasion implements IReport {

  private static final String _XML_ATTRIBUTE_AFFECTED = "affected";
  private static final String _XML_ATTRIBUTE_ROLL = "roll";
  
  private static final String _XML_TAG_HOME = "home";
  private static final String _XML_TAG_AWAY = "away";
  private static final String _XML_TAG_PLAYER = "player";
  
  private List<Integer> fRollsHome;
  private List<Boolean> fPlayersAffectedHome;
  private List<Integer> fRollsAway;
  private List<Boolean> fPlayersAffectedAway;
  
  public ReportKickoffPitchInvasion() {
    fRollsHome = new ArrayList<Integer>();
    fPlayersAffectedHome = new ArrayList<Boolean>();
    fRollsAway = new ArrayList<Integer>();
    fPlayersAffectedAway = new ArrayList<Boolean>();
  }

  public ReportKickoffPitchInvasion(int[] pRollsHome, boolean[] pPlayersAffectedHome, int[] pRollsAway, boolean[] pPlayersAffectedAway) {
    this();
    addRollsHome(pRollsHome);
    addPlayersAffectedHome(pPlayersAffectedHome);
    addRollsAway(pRollsAway);
    addPlayersAffectedAway(pPlayersAffectedAway);
  }
  
  public ReportId getId() {
    return ReportId.KICKOFF_PITCH_INVASION;
  }

  public int[] getRollsHome() {
    int[] rolls = new int[fRollsHome.size()];
    for (int i = 0; i < rolls.length; i++) {
      rolls[i] = fRollsHome.get(i);
    }
    return rolls;
  }
  
  private void addRollHome(int pRoll) {
    fRollsHome.add(pRoll);
  }
  
  private void addRollsHome(int[] pRolls) {
    if (ArrayTool.isProvided(pRolls)) {
      for (int roll : pRolls) {
        addRollHome(roll);
      }
    }
  }
  
  public boolean[] getPlayersAffectedHome() {
    boolean[] playersAffected = new boolean[fPlayersAffectedHome.size()];
    for (int i = 0; i < playersAffected.length; i++) {
      playersAffected[i] = fPlayersAffectedHome.get(i);
    }
    return playersAffected;
  }
  
  private void addPlayerAffectedHome(boolean pPlayerAffected) {
    fPlayersAffectedHome.add(pPlayerAffected);
  }
  
  private void addPlayersAffectedHome(boolean[] pPlayersAffected) {
    if (ArrayTool.isProvided(pPlayersAffected)) {
      for (boolean playerAffected : pPlayersAffected) {
        addPlayerAffectedHome(playerAffected);
      }
    }
  }
  
  public int[] getRollsAway() {
    int[] rolls = new int[fRollsAway.size()];
    for (int i = 0; i < rolls.length; i++) {
      rolls[i] = fRollsAway.get(i);
    }
    return rolls;
  }
  
  private void addRollAway(int pRoll) {
    fRollsAway.add(pRoll);
  }
  
  private void addRollsAway(int[] pRolls) {
    if (ArrayTool.isProvided(pRolls)) {
      for (int roll : pRolls) {
        addRollAway(roll);
      }
    }
  }
  
  public boolean[] getPlayersAffectedAway() {
    boolean[] playersAffected = new boolean[fPlayersAffectedAway.size()];
    for (int i = 0; i < playersAffected.length; i++) {
      playersAffected[i] = fPlayersAffectedAway.get(i);
    }
    return playersAffected;
  }
  
  private void addPlayerAffectedAway(boolean pPlayerAffected) {
    fPlayersAffectedAway.add(pPlayerAffected);
  }
  
  private void addPlayersAffectedAway(boolean[] pPlayersAffected) {
    if (ArrayTool.isProvided(pPlayersAffected)) {
      for (boolean playerAffected : pPlayersAffected) {
        addPlayerAffectedAway(playerAffected);
      }
    }
  }
  
  // transformation
  
  public ReportKickoffPitchInvasion transform() {
    return new ReportKickoffPitchInvasion(getRollsAway(), getPlayersAffectedAway(), getRollsHome(), getPlayersAffectedHome());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    int[] rollsHome = getRollsHome();
    boolean[] playersAffectedHome = getPlayersAffectedHome();
    UtilXml.startElement(pHandler, _XML_TAG_HOME);
    for (int i = 0; i < rollsHome.length; i++) {
      attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, rollsHome[i]);
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AFFECTED, playersAffectedHome[i]);
      UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
    }
    UtilXml.endElement(pHandler, _XML_TAG_HOME);
    int[] rollsAway = getRollsAway();
    boolean[] playersAffectedAway = getPlayersAffectedAway();
    UtilXml.startElement(pHandler, _XML_TAG_AWAY);
    for (int i = 0; i < rollsAway.length; i++) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL, rollsAway[i]);
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_AFFECTED, playersAffectedAway[i]);
      UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
    }
    UtilXml.endElement(pHandler, _XML_TAG_AWAY);
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
    pByteList.addByteArray(getRollsHome());
    pByteList.addBooleanArray(getPlayersAffectedHome());
    pByteList.addByteArray(getRollsAway());
    pByteList.addBooleanArray(getPlayersAffectedAway());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    addRollsHome(pByteArray.getByteArrayAsIntArray());
    addPlayersAffectedHome(pByteArray.getBooleanArray());
    addRollsAway(pByteArray.getByteArrayAsIntArray());
    addPlayersAffectedAway(pByteArray.getBooleanArray());
    return byteArraySerializationVersion;
  }
  
}

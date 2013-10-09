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
public class ReportKickoffThrowARock implements IReport {
  
  private static final String _XML_ATTRIBUTE_PLAYER_ID = "id";
  private static final String _XML_ATTRIBUTE_ROLL_HOME = "rollHome";
  private static final String _XML_ATTRIBUTE_ROLL_AWAY = "rollAway";
  
  private static final String _XML_TAG_PLAYERS_HIT = "playersHit";
  private static final String _XML_TAG_PLAYER = "player";
  
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
    add(pPlayersHit);
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
  
  private void add(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayersHit.add(pPlayerId);
    }
  }
  
  private void add(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        add(playerId);
      }
    }
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportKickoffThrowARock(getRollAway(), getRollHome(), getPlayersHit());
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_HOME, getRollHome());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ROLL_AWAY, getRollAway());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] playersHit = getPlayersHit();
    if (ArrayTool.isProvided(playersHit)) {
      UtilXml.startElement(pHandler, _XML_TAG_PLAYERS_HIT);
      for (String playerId : playersHit) {
        attributes = new AttributesImpl();
        UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PLAYER_ID, playerId);
        UtilXml.addEmptyElement(pHandler, _XML_TAG_PLAYER, attributes);
      }
      UtilXml.endElement(pHandler, _XML_TAG_PLAYERS_HIT);
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
    pByteList.addByte((byte) getRollHome());
    pByteList.addByte((byte) getRollAway());
    pByteList.addStringArray(getPlayersHit());
  }
    
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fRollHome = pByteArray.getByte();
    fRollAway = pByteArray.getByte();
    add(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
      
}

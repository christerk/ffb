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
public class ReportMostValuablePlayers implements IReport {
  
  private static final String _XML_TAG_HOME_PLAYERS = "homePlayers";
  private static final String _XML_TAG_AWAY_PLAYERS = "awayPlayers";
  private static final String _XML_TAG_PLAYER_ID = "playerId";
  
  private List<String> fHomePlayerIds;
  private List<String> fAwayPlayerIds;
  
  public ReportMostValuablePlayers() {
    fHomePlayerIds = new ArrayList<String>();
    fAwayPlayerIds = new ArrayList<String>();
  }
  
  public ReportId getId() {
    return ReportId.MOST_VALUABLE_PLAYERS;
  }

  public void addHomePlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fHomePlayerIds.add(pPlayerId);
    }
  }
  
  private void addHomePlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addHomePlayerId(playerId);
      }
    }
  }

  public String[] getHomePlayerIds() {
    return fHomePlayerIds.toArray(new String[fHomePlayerIds.size()]);
  }
  
  public void addAwayPlayerId(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fAwayPlayerIds.add(pPlayerId);
    }
  }
  
  private void addAwayPlayerIds(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addAwayPlayerId(playerId);
      }
    }
  }

  public String[] getAwayPlayerIds() {
    return fAwayPlayerIds.toArray(new String[fAwayPlayerIds.size()]);
  }

  // transformation
  
  public IReport transform() {
    ReportMostValuablePlayers transformedReport = new ReportMostValuablePlayers();
    transformedReport.addAwayPlayerIds(getHomePlayerIds());
    transformedReport.addHomePlayerIds(getAwayPlayerIds());
    return transformedReport;
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] homePlayerIds = getHomePlayerIds();
    if (ArrayTool.isProvided(homePlayerIds)) {
      UtilXml.startElement(pHandler, _XML_TAG_HOME_PLAYERS, attributes);
      for (String playerId : homePlayerIds) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_ID, playerId);
      }
      UtilXml.endElement(pHandler, _XML_TAG_HOME_PLAYERS);
    }
    String[] awayPlayerIds = getAwayPlayerIds();
    if (ArrayTool.isProvided(awayPlayerIds)) {
      UtilXml.startElement(pHandler, _XML_TAG_AWAY_PLAYERS, attributes);
      for (String playerId : awayPlayerIds) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_ID, playerId);
      }
      UtilXml.endElement(pHandler, _XML_TAG_AWAY_PLAYERS);
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
    pByteList.addStringArray(getHomePlayerIds());
    pByteList.addStringArray(getAwayPlayerIds());
  }
  
  public int initFrom(ByteArray pByteArray) {
    ReportId reportId = ReportId.fromId(pByteArray.getSmallInt());
    if (getId() != reportId) {
      throw new IllegalStateException("Wrong report id. Expected " + getId().getName() + " received " + ((reportId != null) ? reportId.getName() : "null"));
    }
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    addHomePlayerIds(pByteArray.getStringArray());
    addAwayPlayerIds(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }
  
}

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
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportMostValuablePlayers implements IReport {

  private static final String _XML_TAG_HOME_PLAYERS = "homePlayers";
  private static final String _XML_TAG_AWAY_PLAYERS = "awayPlayers";
  private static final String _XML_TAG_PLAYER_ID = "playerId";

  private List<String> fPlayerIdsHome;
  private List<String> fPlayerIdsAway;

  public ReportMostValuablePlayers() {
    fPlayerIdsHome = new ArrayList<String>();
    fPlayerIdsAway = new ArrayList<String>();
  }

  public ReportId getId() {
    return ReportId.MOST_VALUABLE_PLAYERS;
  }

  public void addPlayerIdHome(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIdsHome.add(pPlayerId);
    }
  }

  private void addPlayerIdsHome(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addPlayerIdHome(playerId);
      }
    }
  }

  public String[] getPlayerIdsHome() {
    return fPlayerIdsHome.toArray(new String[fPlayerIdsHome.size()]);
  }

  public void addPlayerIdAway(String pPlayerId) {
    if (StringTool.isProvided(pPlayerId)) {
      fPlayerIdsAway.add(pPlayerId);
    }
  }

  private void addPlayerIdsAway(String[] pPlayerIds) {
    if (ArrayTool.isProvided(pPlayerIds)) {
      for (String playerId : pPlayerIds) {
        addPlayerIdAway(playerId);
      }
    }
  }

  public String[] getPlayerIdsAway() {
    return fPlayerIdsAway.toArray(new String[fPlayerIdsAway.size()]);
  }

  // transformation

  public IReport transform() {
    ReportMostValuablePlayers transformedReport = new ReportMostValuablePlayers();
    transformedReport.addPlayerIdsAway(getPlayerIdsHome());
    transformedReport.addPlayerIdsHome(getPlayerIdsAway());
    return transformedReport;
  }

  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    String[] homePlayerIds = getPlayerIdsHome();
    if (ArrayTool.isProvided(homePlayerIds)) {
      UtilXml.startElement(pHandler, _XML_TAG_HOME_PLAYERS, attributes);
      for (String playerId : homePlayerIds) {
        UtilXml.addValueElement(pHandler, _XML_TAG_PLAYER_ID, playerId);
      }
      UtilXml.endElement(pHandler, _XML_TAG_HOME_PLAYERS);
    }
    String[] awayPlayerIds = getPlayerIdsAway();
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
    pByteList.addStringArray(getPlayerIdsHome());
    pByteList.addStringArray(getPlayerIdsAway());
  }

  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    addPlayerIdsHome(pByteArray.getStringArray());
    addPlayerIdsAway(pByteArray.getStringArray());
    return byteArraySerializationVersion;
  }

  // JSON serialization

  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_IDS_HOME.addTo(jsonObject, getPlayerIdsHome());
    IJsonOption.PLAYER_IDS_AWAY.addTo(jsonObject, getPlayerIdsAway());
    return jsonObject;
  }

  public ReportMostValuablePlayers initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    addPlayerIdsHome(IJsonOption.PLAYER_IDS_HOME.getFrom(jsonObject));
    addPlayerIdsAway(IJsonOption.PLAYER_IDS_AWAY.getFrom(jsonObject));
    return this;
  }

}

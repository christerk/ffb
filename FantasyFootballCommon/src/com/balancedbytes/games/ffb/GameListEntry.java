package com.balancedbytes.games.ffb;

import java.util.Date;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.xml.IXmlReadable;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class GameListEntry implements IXmlSerializable, IByteArrayReadable, IJsonSerializable {
    
//  <gameList>
//    <game id="4765261" started="2009-05-05T11:50:20.345">
//      <homeTeam id="4326809" name="Kalimars Elves" coach="Kalimar" />
//      <awayTeam id="5424335" name="BattleLores Orcs" coach="BattleLore" />
//    </game>
//  </gameList>

  public static final String XML_TAG = "game";

  private static final String _XML_ATTRIBUTE_ID = "id";
  private static final String _XML_ATTRIBUTE_STARTED = "started";
  private static final String _XML_ATTRIBUTE_NAME = "name";
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  
  private static final String _XML_TAG_HOME_TEAM = "homeTeam";
  private static final String _XML_TAG_AWAY_TEAM = "awayTeam";
  
  private long fGameId;
  private Date fStarted;
  private String fTeamHomeId;
  private String fTeamHomeName;
  private String fTeamHomeCoach;
  private String fTeamAwayId;
  private String fTeamAwayName;
  private String fTeamAwayCoach;
  
  public GameListEntry() {
    super();
  }
  
  public void init(Game pGame) {
    if (pGame != null) {
      setGameId(pGame.getId());
      setStarted(pGame.getStarted());
      setTeamHomeId(pGame.getTeamHome().getId());
      setTeamHomeName(pGame.getTeamHome().getName());
      setTeamHomeCoach(pGame.getTeamHome().getCoach());
      setTeamAwayId(pGame.getTeamAway().getId());
      setTeamAwayName(pGame.getTeamAway().getName());
      setTeamAwayCoach(pGame.getTeamAway().getCoach());
    }
  }
  
  public long getGameId() {
    return fGameId;
  }

  public void setGameId(long pGameId) {
    fGameId = pGameId;
  }

  public Date getStarted() {
    return fStarted;
  }

  public void setStarted(Date pStarted) {
    fStarted = pStarted;
  }

  public String getTeamHomeId() {
    return fTeamHomeId;
  }

  public void setTeamHomeId(String pTeamHomeId) {
    fTeamHomeId = pTeamHomeId;
  }

  public String getTeamHomeName() {
    return fTeamHomeName;
  }

  public void setTeamHomeName(String pTeamHomeName) {
    fTeamHomeName = pTeamHomeName;
  }

  public String getTeamHomeCoach() {
    return fTeamHomeCoach;
  }

  public void setTeamHomeCoach(String pTeamHomeCoach) {
    fTeamHomeCoach = pTeamHomeCoach;
  }

  public String getTeamAwayId() {
    return fTeamAwayId;
  }

  public void setTeamAwayId(String pTeamAwayId) {
    fTeamAwayId = pTeamAwayId;
  }

  public String getTeamAwayName() {
    return fTeamAwayName;
  }

  public void setTeamAwayName(String pTeamAwayName) {
    fTeamAwayName = pTeamAwayName;
  }

  public String getTeamAwayCoach() {
    return fTeamAwayCoach;
  }

  public void setTeamAwayCoach(String pTeamAwayCoach) {
    fTeamAwayCoach = pTeamAwayCoach;
  }
  
  // XML serialization

  public void addToXml(TransformerHandler pHandler) {
  	
  	AttributesImpl attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getGameId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STARTED, getStarted());
  	UtilXml.startElement(pHandler, XML_TAG, attributes);

		attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getTeamHomeId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, getTeamHomeName());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getTeamHomeCoach());
		UtilXml.startElement(pHandler, _XML_TAG_HOME_TEAM, attributes);
		UtilXml.endElement(pHandler, _XML_TAG_HOME_TEAM);
  	
		attributes = new AttributesImpl();
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getTeamAwayId());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NAME, getTeamAwayName());
  	UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getTeamAwayCoach());
		UtilXml.startElement(pHandler, _XML_TAG_AWAY_TEAM, attributes);
		UtilXml.endElement(pHandler, _XML_TAG_AWAY_TEAM);
  	
  	UtilXml.endElement(pHandler, XML_TAG);
  	
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlReadable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    if (XML_TAG.equals(pXmlTag)) {
      fGameId = UtilXml.getLongAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
      fStarted = UtilXml.getTimestampAttribute(pXmlAttributes, _XML_ATTRIBUTE_STARTED);
    }
    if (_XML_TAG_HOME_TEAM.equals(pXmlTag)) {
      fTeamHomeId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
      fTeamHomeName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME);
      fTeamHomeCoach = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_COACH);
    }
    if (_XML_TAG_AWAY_TEAM.equals(pXmlTag)) {
      fTeamAwayId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
      fTeamAwayName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_NAME);
      fTeamAwayCoach = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_COACH);
    }
    return this;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    return XML_TAG.equals(pXmlTag);
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addLong(getGameId());
    pByteList.addLong((long) ((getStarted() != null) ? getStarted().getTime() : 0));
    pByteList.addString(getTeamHomeId());
    pByteList.addString(getTeamHomeName());
    pByteList.addString(getTeamHomeCoach());
    pByteList.addString(getTeamAwayId());
    pByteList.addString(getTeamAwayName());
    pByteList.addString(getTeamAwayCoach());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fGameId = pByteArray.getLong();
    long startedTime = pByteArray.getLong();
    if (startedTime > 0) {
      fStarted = new Date(startedTime);
    } else {
      fStarted = null;
    }
    fTeamHomeId = pByteArray.getString();
    fTeamHomeName = pByteArray.getString();
    fTeamHomeCoach = pByteArray.getString();
    fTeamAwayId = pByteArray.getString();
    fTeamAwayName = pByteArray.getString();
    fTeamAwayCoach = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
    IJsonOption.STARTED.addTo(jsonObject, fStarted);
    IJsonOption.TEAM_HOME_ID.addTo(jsonObject, fTeamHomeId);
    IJsonOption.TEAM_HOME_NAME.addTo(jsonObject, fTeamHomeName);
    IJsonOption.TEAM_HOME_COACH.addTo(jsonObject, fTeamHomeCoach);
    IJsonOption.TEAM_AWAY_ID.addTo(jsonObject, fTeamAwayId);
    IJsonOption.TEAM_AWAY_NAME.addTo(jsonObject, fTeamAwayName);
    IJsonOption.TEAM_AWAY_COACH.addTo(jsonObject, fTeamAwayCoach);
    return jsonObject;
  }
  
  public GameListEntry initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGameId = IJsonOption.GAME_ID.getFrom(jsonObject);
    fStarted = IJsonOption.STARTED.getFrom(jsonObject);    
    fTeamHomeId = IJsonOption.TEAM_HOME_ID.getFrom(jsonObject);
    fTeamHomeName = IJsonOption.TEAM_HOME_NAME.getFrom(jsonObject);
    fTeamHomeCoach = IJsonOption.TEAM_HOME_COACH.getFrom(jsonObject);
    fTeamAwayId = IJsonOption.TEAM_AWAY_ID.getFrom(jsonObject);
    fTeamAwayName = IJsonOption.TEAM_AWAY_NAME.getFrom(jsonObject);
    fTeamAwayCoach = IJsonOption.TEAM_AWAY_COACH.getFrom(jsonObject);
    return this;
  }

}

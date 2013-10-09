package com.balancedbytes.games.ffb.model;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class GameResult implements IXmlSerializable, IByteArraySerializable {
  
  public static final String XML_TAG = "gameResult";
  
  private static final String _XML_ATTRIBUTE_REPLAY_ID = "replayId";
    
  private Game fGame;
  
  private TeamResult fTeamResultHome;
  private TeamResult fTeamResultAway;
  
  private transient boolean fTeamResultHomeInitialized;
  
  public GameResult(Game pGame) {
    this(pGame, null, null);
  }
  
  private GameResult(Game pGame, TeamResult pTeamResultHome, TeamResult pTeamResultAway) {
    fGame = pGame;
    fTeamResultHome = pTeamResultHome;
    if (fTeamResultHome == null) {
      fTeamResultHome = new TeamResult(this, true, fGame.getTeamHome());
    }
    fTeamResultAway = pTeamResultAway;
    if (fTeamResultAway == null) {
      fTeamResultAway = new TeamResult(this, false, fGame.getTeamAway());
    }
  }
  
  public Game getGame() {
    return fGame;
  }
   
  public TeamResult getTeamResultHome() {
    return fTeamResultHome;
  }
  
  public TeamResult getTeamResultAway() {
    return fTeamResultAway;
  }
  
  public int getScoreHome() {
    return getTeamResultHome().getScore();
  }
  
  public int getScoreAway() {
    return getTeamResultAway().getScore();
  }
  
  public GameResult transform() {
    TeamResult transformedTeamResultHome = new TeamResult(this, true, getTeamResultAway().getTeam());
    transformedTeamResultHome.init(getTeamResultAway());
    TeamResult transformedTeamResultAway = new TeamResult(this, false, getTeamResultHome().getTeam());
    transformedTeamResultAway.init(getTeamResultHome());
    return new GameResult(getGame(), transformedTeamResultHome, transformedTeamResultAway);
  }
  
  public PlayerResult getPlayerResult(Player pPlayer) {
    if (getGame().getTeamHome().hasPlayer(pPlayer)) {
      return getTeamResultHome().getPlayerResult(pPlayer);
    } else {
      return getTeamResultAway().getPlayerResult(pPlayer);
    }
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_REPLAY_ID, (getGame() != null) ? getGame().getId() : 0L);
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    
    getTeamResultHome().addToXml(pHandler);
    
    getTeamResultAway().addToXml(pHandler);

    UtilXml.endElement(pHandler, XML_TAG);
    
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }
  
  public IXmlSerializable startXmlElement(String pXmlTag, Attributes pXmlAttributes) {
    IXmlSerializable xmlElement = this;
    if (TeamResult.XML_TAG.equals(pXmlTag)) {
      if (fTeamResultHomeInitialized) {
        fTeamResultAway.startXmlElement(pXmlTag, pXmlAttributes);
        xmlElement = fTeamResultAway;
      } else {
        fTeamResultHome.startXmlElement(pXmlTag, pXmlAttributes);
        xmlElement = fTeamResultHome;
        fTeamResultHomeInitialized = true;
      }
    }
    return xmlElement;
  }
  
  public boolean endXmlElement(String pXmlTag, String pValue) {
    boolean complete = XML_TAG.equals(pXmlTag); 
    if (complete) {
      if (getTeamResultHome().getTeam() != getGame().getTeamHome()) {
        TeamResult teamResultAway = getTeamResultHome();
        fTeamResultHome = getTeamResultAway();
        fTeamResultAway = teamResultAway;
      }
    }
    return complete;
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 1;
  }  

  public void addTo(ByteList pByteList) {
    pByteList.addByte((byte) getByteArraySerializationVersion());
    getTeamResultHome().addTo(pByteList);
    getTeamResultAway().addTo(pByteList);
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getByte();
    getTeamResultHome().initFrom(pByteArray);
    getTeamResultAway().initFrom(pByteArray);
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.TEAM_RESULT_HOME.addTo(jsonObject, fTeamResultHome.toJsonValue());
    IJsonOption.TEAM_RESULT_AWAY.addTo(jsonObject, fTeamResultAway.toJsonValue());
    return jsonObject;
  }
  
  public GameResult initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fTeamResultHome.initFrom(IJsonOption.TEAM_RESULT_HOME.getFrom(jsonObject));
    fTeamResultAway.initFrom(IJsonOption.TEAM_RESULT_AWAY.getFrom(jsonObject));
    return this;
  }

}

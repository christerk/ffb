package com.balancedbytes.games.ffb.net.commands;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ClientModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandJoin extends NetCommand {
  
  private static final String _XML_ATTRIBUTE_COACH = "coach";
  private static final String _XML_ATTRIBUTE_PASSWORD = "password";
  private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";
  private static final String _XML_ATTRIBUTE_GAME_NAME = "gameName";
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_TEAM_NAME = "teamName";
  private static final String _XML_ATTRIBUTE_CLIENT_MODE = "clientMode";
  
  private String fCoach;
  private String fPassword;
  private long fGameId;
  private String fGameName;
  private ClientMode fClientMode;
  private String fTeamId;
  private String fTeamName;
  
  public ClientCommandJoin() {
    super();
  }

  public ClientCommandJoin(ClientMode pClientMode) {
    fClientMode = pClientMode;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_JOIN;
  }
  
  public ClientMode getClientMode() {
    return fClientMode;
  }
  
  public void setClientMode(ClientMode pClientMode) {
    fClientMode = pClientMode;
  }
  
  public String getCoach() {
    return fCoach;
  }

  public void setCoach(String pCoach) {
    fCoach = pCoach;
  }
  
  public String getPassword() {
    return fPassword;
  }
  
  public void setPassword(String pPassword) {
    fPassword = pPassword;
  }
  
  public long getGameId() {
    return fGameId;
  }
  
  public void setGameId(long pGameId) {
    fGameId = pGameId;
  }

  public String getGameName() {
    return fGameName;
  }
  
  public void setGameName(String pGameName) {
    fGameName = pGameName;
  }

  public String getTeamId() {
    return fTeamId;
  }
  
  public void setTeamId(String pTeamId) {
    fTeamId = pTeamId;
  }
  
  public String getTeamName() {
    return fTeamName;
  }
  
  public void setTeamName(String pTeamName) {
    fTeamName = pTeamName;
  }
  
  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
  	AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_CLIENT_MODE, (getClientMode() != null) ? getClientMode().getName() : null);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_COACH, getCoach());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_PASSWORD, getPassword());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, Long.toString(getGameId()));
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_NAME, getGameName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, getTeamId());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_NAME, getTeamName());
  	UtilXml.addEmptyElement(pHandler, getId().getName(), attributes);
  }
  
  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
  }

  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) getClientMode().getId());
    pByteList.addString(getCoach());
    pByteList.addString(getPassword());
    pByteList.addLong(getGameId());
    pByteList.addString(getGameName());
    pByteList.addString(getTeamId());
    pByteList.addString(getTeamName());
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fClientMode = new ClientModeFactory().forId(pByteArray.getByte());
    fCoach = pByteArray.getString();
    fPassword = pByteArray.getString();
    fGameId = pByteArray.getLong();
    fGameName = pByteArray.getString();
    fTeamId = pByteArray.getString();
    fTeamName = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.PASSWORD.addTo(jsonObject, fPassword);
    IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
    IJsonOption.GAME_NAME.addTo(jsonObject, fGameName);
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    IJsonOption.TEAM_NAME.addTo(jsonObject, fTeamName);
    return jsonObject;
  }

  public void initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.asJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(jsonObject);
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fPassword = IJsonOption.PASSWORD.getFrom(jsonObject);
    fGameId = IJsonOption.GAME_ID.getFrom(jsonObject);
    fGameName = IJsonOption.GAME_NAME.getFrom(jsonObject);
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    fTeamName = IJsonOption.TEAM_NAME.getFrom(jsonObject);
  }
      
}

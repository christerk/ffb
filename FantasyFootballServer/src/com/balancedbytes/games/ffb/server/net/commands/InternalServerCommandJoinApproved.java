package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ClientModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandJoinApproved extends InternalServerCommand {
  
  private String fCoach;
  private String fGameName;
  private ClientMode fClientMode;
  private String fTeamId;
  
  public InternalServerCommandJoinApproved(long pGameId, String pGameName, String pCoach, String pTeamId, ClientMode pClientMode) {
    super(pGameId);
    fGameName = pGameName;
    fCoach = pCoach;
    fTeamId = pTeamId;
    fClientMode = pClientMode;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_JOIN_APPROVED;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String getGameName() {
    return fGameName;
  }
  
  public ClientMode getClientMode() {
    return fClientMode;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addString(fCoach);
    pByteList.addString(fGameName);
    pByteList.addByte((byte) ((fClientMode != null) ? fClientMode.getId() : 0));
    pByteList.addString(fTeamId);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fCoach = pByteArray.getString();
    fGameName = pByteArray.getString();
    fClientMode = new ClientModeFactory().forId(pByteArray.getByte());
    fTeamId = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.COACH.addTo(jsonObject, fCoach);
    IJsonOption.GAME_NAME.addTo(jsonObject, fGameName);
    IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
    IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
    return jsonObject;
  }

  public InternalServerCommandJoinApproved initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fCoach = IJsonOption.COACH.getFrom(jsonObject);
    fGameName = IJsonOption.GAME_NAME.getFrom(jsonObject);
    fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(jsonObject);
    fTeamId = IJsonOption.TEAM_ID.getFrom(jsonObject);
    return this;
  }
 
}

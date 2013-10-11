package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamList extends ServerCommand {
 
  private TeamList fTeamList;
  
  public ServerCommandTeamList() {
    super();
  }
  
  public ServerCommandTeamList(TeamList pTeamList) {
    this();
    fTeamList = pTeamList;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_TEAM_LIST;
  }

  public TeamList getTeamList() {
    return fTeamList;
  }
  
  public boolean isReplayable() {
    return false;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(getCommandNr());
    boolean hasTeamList = (getTeamList() != null);
    pByteList.addBoolean(hasTeamList);
    if (hasTeamList) {
      getTeamList().addTo(pByteList);
    }
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    boolean hasTeamList = pByteArray.getBoolean();
    if (hasTeamList) {
      fTeamList = new TeamList();
      fTeamList.initFrom(pByteArray);
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    if (fTeamList != null) {
      IJsonOption.TEAM_LIST.addTo(jsonObject, fTeamList.toJsonValue());
    }
    return jsonObject;
  }
  
  public ServerCommandTeamList initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    JsonObject teamListObject = IJsonOption.TEAM_LIST.getFrom(jsonObject);
    if (teamListObject != null) {
      fTeamList = new TeamList().initFrom(teamListObject);
    } else {
      fTeamList = null;
    }
    return this;
  }
    
}

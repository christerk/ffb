package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



/**
 * 
 * @author Kalimar
 */
public class ServerCommandStatus extends ServerCommand {
  
  private ServerStatus fServerStatus;
  private String fMessage;
  
  public ServerCommandStatus() {
    super();
  }
  
  public ServerCommandStatus(ServerStatus pServerStatus, String pMessage) {
    fServerStatus = pServerStatus;
    fMessage = pMessage;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_STATUS;
  }

  public ServerStatus getServerStatus() {
    return fServerStatus;
  }
  
  public String getMessage() {
    return fMessage;
  }
  
  public boolean isReplayable() {
    return false;
  }

  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.SERVER_STATUS.addTo(jsonObject, fServerStatus);
    IJsonOption.MESSAGE.addTo(jsonObject, fMessage);
    return jsonObject;
  }

  public ServerCommandStatus initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fServerStatus = (ServerStatus) IJsonOption.SERVER_STATUS.getFrom(jsonObject);
    fMessage = IJsonOption.MESSAGE.getFrom(jsonObject);
    return this;
  }

 }

package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ClientCommandApothecaryChoice extends NetCommand {
   
  private String fPlayerId;
  private PlayerState fPlayerState;
  private SeriousInjury fSeriousInjury;
  
  public ClientCommandApothecaryChoice() {
    super();
  }

  public ClientCommandApothecaryChoice(String pPlayerId, PlayerState pPlayerState, SeriousInjury pSeriousInjury) {
    fPlayerId = pPlayerId;
    fPlayerState = pPlayerState;
    fSeriousInjury = pSeriousInjury;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_APOTHECARY_CHOICE;
  }
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public PlayerState getPlayerState() {
    return fPlayerState;
  }
  
  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addString(getPlayerId());
    pByteList.addSmallInt((getPlayerState() != null) ? getPlayerState().getId() : 0);
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fPlayerId = pByteArray.getString();
    fPlayerState = new PlayerState(pByteArray.getSmallInt());
    fSeriousInjury = new SeriousInjuryFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }

  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
    IJsonOption.PLAYER_STATE.addTo(jsonObject, fPlayerState);
    IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
    return jsonObject;
  }
  
  public ClientCommandApothecaryChoice initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fPlayerId = IJsonOption.PLAYER_ID.getFrom(jsonObject);
    fPlayerState = IJsonOption.PLAYER_STATE.getFrom(jsonObject);
    fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(jsonObject);
    return this;
  }
  
}

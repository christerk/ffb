package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ConcedeGameStatus;
import com.balancedbytes.games.ffb.ConcedeGameStatusFactory;
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
public class ClientCommandConcedeGame extends NetCommand {
  
  private ConcedeGameStatus fConcedeGameStatus;
  
  public ClientCommandConcedeGame() {
    super();
  }

  public ClientCommandConcedeGame(ConcedeGameStatus pConcedeGameStatus) {
    fConcedeGameStatus = pConcedeGameStatus;
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_CONCEDE_GAME;
  }
  
  public ConcedeGameStatus getConcedeGameStatus() {
    return fConcedeGameStatus;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getConcedeGameStatus() != null) ? getConcedeGameStatus().getId() : 0));
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fConcedeGameStatus = new ConcedeGameStatusFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.CONCEDE_GAME_STATUS.addTo(jsonObject, fConcedeGameStatus);
    return jsonObject;
  }
  
  public ClientCommandConcedeGame initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fConcedeGameStatus = (ConcedeGameStatus) IJsonOption.CONCEDE_GAME_STATUS.getFrom(jsonObject);
    return this;
  }
    
}

package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.SoundFactory;
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
public class ServerCommandSound extends ServerCommand {
  
  private Sound fSound;
  
  public ServerCommandSound() {
    super();
  }

  public ServerCommandSound(Sound pSound) {
    fSound = pSound;
  }
  
  public NetCommandId getId() {
    return NetCommandId.SERVER_SOUND;
  }
  
  public Sound getSound() {
    return fSound;
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
    pByteList.addByte((byte) getSound().getId()); 
  }
    
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setCommandNr(pByteArray.getSmallInt());
    fSound = new SoundFactory().forId(pByteArray.getByte());
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
    IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
    IJsonOption.SOUND.addTo(jsonObject, fSound);
    return jsonObject;
  }

  public ServerCommandSound initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
    fSound = (Sound) IJsonOption.SOUND.getFrom(jsonObject);
    return this;
  }
    
}

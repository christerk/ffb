package com.balancedbytes.games.ffb.server.net.commands;

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
public class InternalServerCommandReplayLoaded extends InternalServerCommand {
  
  private int fReplayToCommandNr;
  
  public InternalServerCommandReplayLoaded(long pGameId, int pReplayToCommandNr) {
    super(pGameId);
    fReplayToCommandNr = pReplayToCommandNr;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_REPLAY_LOADED;
  }
 
  public int getReplayToCommandNr() {
    return fReplayToCommandNr;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    super.addTo(pByteList);
    pByteList.addSmallInt(fReplayToCommandNr);
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = super.initFrom(pByteArray);
    fReplayToCommandNr = pByteArray.getSmallInt();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization

  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IJsonOption.REPLAY_TO_COMMAND_NR.addTo(jsonObject, fReplayToCommandNr);
    return jsonObject;
  }

  public InternalServerCommandReplayLoaded initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
    fReplayToCommandNr = IJsonOption.REPLAY_TO_COMMAND_NR.getFrom(jsonObject);
    return this;
  }
 
}
